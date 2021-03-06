package com.dandanakka.datastore;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.dandanakka.core.util.ConfigUtil;
import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.Application;
import com.dandanakka.datastore.model.Attribute;
import com.dandanakka.datastore.model.Id;
import com.dandanakka.datastore.model.LocaleSpecific;
import com.dandanakka.datastore.model.Operator;
import com.dandanakka.datastore.model.PaginatedResult;
import com.dandanakka.datastore.model.Query;
import com.dandanakka.datastore.model.Reference;
import com.dandanakka.datastore.model.Schema;

public abstract class DataStore {

	private static final Map<String, DataStore> dataStoreMap = new HashMap<String, DataStore>();

	private Application application;

	public Application getApplication() {
		return application;
	}

	public void saveApplication(Application applicationToSave)
			throws DataStoreException {
		if (!application.getName().equals("application")) {
			applicationToSave.setName(application.getName());
			getDataStore("application").saveObject(applicationToSave, true);
			setApplication(application.getName());
		}
	}

	private void setApplication(final String applicationName)
			throws DataStoreException {
		if (applicationName.equals("application")) {
			application = new Application();
			application.setName("application");
			try {
				application.setLabel(ConfigUtil
						.getCongiguration("admin.app.label"));
				application.setDescription(ConfigUtil
						.getCongiguration("admin.app.description"));
			} catch (IOException e) {
				throw new DataStoreException(
						"please check config file instance", e);
			}

		} else {
			application = getDataStore("application").getObject(
					Application.class, applicationName);
		}
	}

	public static DataStore getDataStore(final String applicationName)
			throws DataStoreException {
		DataStore dataStore = dataStoreMap.get(applicationName);
		if (dataStore == null) {
			try {
				dataStore = (DataStore) Class.forName(
						ConfigUtil.getCongiguration("datastore")).newInstance();
				dataStore.setApplication(applicationName);
				dataStoreMap.put(applicationName, dataStore);
			} catch (InstantiationException e) {
				throw new DataStoreException("please check MongoDB instance", e);
			} catch (IllegalAccessException e) {
				throw new DataStoreException("please check MongoDB instance", e);
			} catch (ClassNotFoundException e) {
				throw new DataStoreException("please check MongoDB instance", e);
			} catch (IOException e) {
				throw new DataStoreException(
						"please check config file instance", e);
			}
		}
		return dataStore;
	}

	public Field getIdColumn(Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {

			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(Id.class)) {
					return field;

				}
			}
		}
		return null;
	}

	public <T> T getObject(Class<T> clazz, String id) throws DataStoreException {
		return getObject(clazz, id, null);
	}

	public <T> T getObject(Class<T> clazz, String id, String locale)
			throws DataStoreException {
		T obj = null;
		Map<String, Object> dataMap = getDataMap(getSchemaName(clazz), id);
		obj = getObject(clazz, dataMap, locale);
		return obj;
	}

	private String getSchemaName(Class clazz) {
		String name = clazz.getName();
		int index = name.lastIndexOf('.');
		if (index != -1) {
			name = name.substring(index + 1) + index;
		} else {
			name = name + "0";
		}
		return name;
	}

	private void removeNullValues(Map<String, Object> map) {
		if (map != null) {
			Set<String> keys = map.keySet();
			List<String> nullKeys = new ArrayList<String>();

			for (String key : keys) {
				if (map.get(key) == null) {
					nullKeys.add(key);
				}
			}

			for (String string : nullKeys) {
				keys.remove(string);
			}
		}

	}

	public Map<String, Object> getDataMap(Object data) {
		return getDataMap(data, null);
	}

	public Map<String, Object> getDataMap(Object data, String locale) {
		Map<String, Object> dataMap = null;

		ObjectMapper m = new ObjectMapper();
		dataMap = m.convertValue(data, Map.class);

		if (locale != null) {
			Field[] fields = getLocaleSpecificFields(data.getClass());
			for (Field field : fields) {
				dataMap.put(field.getName() + "_" + locale,
						dataMap.remove(field.getName()));
			}
		}

		// Remove Reference Values
		List<Field> referenceFields = getReferenceColumns(data.getClass());
		for (Field field : referenceFields) {
			dataMap.remove(field.getName());
		}

		return dataMap;
	}

	private <T> T getObject(Class<T> clazz, Map<String, Object> dataMap,
			String locale) throws DataStoreException {
		T obj = null;
		if (dataMap != null) {

			ObjectMapper m = new ObjectMapper();
			// Set Id Value
			String idAttributeName = getIdColumn(clazz).getName();
			dataMap.put(idAttributeName, dataMap.get(getIdColumnName())
					.toString());
			dataMap.remove(getIdColumnName());

			// Set Reference Values
			List<Field> referenceFields = getReferenceColumns(clazz);
			for (Field field : referenceFields) {
				Reference reference = field.getAnnotation(Reference.class);
				Query query = new Query();
				query.addCriteria(reference.targetAtribute(), Operator.EQUALS,
						dataMap.get(idAttributeName));

				dataMap.put(field.getName(),
						getDataList(reference.targetSchema(), query, locale));
			}

			// Set Locale Specific Fields
			reconcileLocaleFields(dataMap, locale);

			obj = m.convertValue(dataMap, clazz);
		}
		return obj;
	}

	private void reconcileLocaleFields(Map<String, Object> dataMap,
			String locale) {
		String localemarker = "_" + locale;
		Object[] keys = dataMap.keySet().toArray();
		for (Object key : keys) {
			if (key.toString().indexOf(localemarker) != -1) {
				Object value = dataMap.remove(key);
				dataMap.put(key.toString().replaceAll(localemarker, ""), value);
			} else if (key.toString().indexOf('_') != -1) {
				dataMap.remove(key);
			}
		}
	}

	public List<Field> getReferenceColumns(Class clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {

			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(Reference.class)) {
					fields.add(field);

				}
			}
		}
		return fields;
	}

	public <T> List<T> getDataList(Class<T> clazz, Query query)
			throws DataStoreException {

		return getDataList(clazz, query, null);
	}

	public <T> List<T> getDataList(Class<T> clazz) throws DataStoreException {
		return getDataList(clazz, (Query) null, (String) null);
	}

	public <T> List<T> getDataList(Class<T> clazz, String locale)
			throws DataStoreException {
		return getDataList(clazz, (Query) null, locale);
	}

	public <T> List<T> getDataList(Class<T> clazz, Query query, String locale)
			throws DataStoreException {
		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;

		paginatedResult = getDataList(getSchemaName(clazz), query, null, null);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(clazz, map, locale));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult == null ? null : paginatedResult.getResults();
	}

	public <T> List<T> getDataList(T data) throws DataStoreException {
		return getDataList(data, null);

	}

	public <T> List<T> getDataList(T data, String locale)
			throws DataStoreException {
		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;
		Map<String, Object> query = getDataMap(data, locale);
		removeNullValues(query);

		paginatedResult = getDataList(getSchemaName(data.getClass()), query,
				null, null);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(data.getClass(), map, locale));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult == null ? null : paginatedResult.getResults();
	}

	public <T> PaginatedResult<T> getDataList(T data, int pageNumber,
			int pageSize) throws DataStoreException {
		return getDataList(data, null, pageNumber, pageSize);
	}

	public <T> PaginatedResult<T> getDataList(T data, String locale,
			int pageNumber, int pageSize) throws DataStoreException {

		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;
		Map<String, Object> query = getDataMap(data, locale);
		removeNullValues(query);

		paginatedResult = getDataList(getSchemaName(data.getClass()), query,
				pageNumber, pageSize);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(data.getClass(), map, locale));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult;
	}

	private String saveData(String schemaName, Map<String, Object> dataMap)
			throws DataStoreException {
		String generatedId = null;
		String idName = getIdColumnName();
		Object idValue = dataMap.get(idName);
		dataMap.remove(idName);

		if (idValue == null || idValue.toString().trim().length() == 0) {
			generatedId = createData(schemaName, dataMap);
		} else {
			dataMap.put(getIdColumnName(), idValue);
			updateData(schemaName, dataMap);
		}
		return generatedId;
	}

	public String saveObject(Object data) throws DataStoreException {
		return saveObject(data, null, false);
	}

	public String saveObject(Object data, String locale)
			throws DataStoreException {
		return saveObject(data, locale, false);
	}

	public String saveObject(Object data, boolean withoutNull)
			throws DataStoreException {
		return saveObject(data, null, withoutNull);
	}

	public String saveObject(Object data, String locale, boolean withoutNull)
			throws DataStoreException {
		String returnValue = null;
		if (data != null) {
			Map<String, Object> dataMap = getDataMap(data, locale);
			Field idField = getIdColumn(data.getClass());
			String idName = idField.getName();
			Object idValue = dataMap.get(idName);
			dataMap.remove(idName);
			if (withoutNull) {
				removeNullValues(dataMap);
			}
			if (idField.getAnnotation(Id.class).auto()) {
				if (idValue == null || idValue.toString().trim().length() == 0) {
					returnValue = createData(getSchemaName(data.getClass()),
							dataMap);
				} else {
					dataMap.put(getIdColumnName(), idValue);
					updateData(getSchemaName(data.getClass()), dataMap);
				}
			} else {
				dataMap.put(getIdColumnName(), idValue);
				if (updateData(getSchemaName(data.getClass()), dataMap) == 0) {
					returnValue = createData(getSchemaName(data.getClass()),
							dataMap);
				}

			}
			if (returnValue == null) {
				returnValue = idValue.toString();
			}

		}

		return returnValue;
	}

	public boolean deleteData(Class schemaClass, String id)
			throws DataStoreException {
		return deleteData(getSchemaName(schemaClass), id);
	}

	public Field[] getLocaleSpecificFields(Class clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Field field : clazz.getDeclaredFields()) {

			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(LocaleSpecific.class)) {
					fields.add(field);

				}
			}
		}
		return fields.toArray(new Field[0]);
	}

	public void translate(String locale, Class clazz) throws DataStoreException {

		if (locale != null) {
			List<Object> objects = getDataList(clazz);
			Map<String, Object> dataMap = null;
			Object valueToTranslate = null;
			String schemaName = getSchemaName(clazz);
			Map<String, Object> localeDataMap = null;

			for (Object object : objects) {
				Field[] fields = getLocaleSpecificFields(clazz);
				dataMap = getDataMap(object);
				localeDataMap = new HashMap<String, Object>();

				localeDataMap.put(getIdColumnName(), dataMap.remove(getIdColumn(clazz).getName()));
				for (Field field : fields) {
					valueToTranslate = dataMap.get(field.getName());
					if (valueToTranslate != null
							&& valueToTranslate instanceof String
							&& valueToTranslate.toString().trim().length() < 50) {

						localeDataMap.put(field.getName() + "_" + locale,
								transtale(valueToTranslate.toString(), locale));
						saveData(schemaName, localeDataMap);
					}
				}
			}

		}
	}

	private String transtale(String value, String locale) {
		return value + "_" + locale;
	}

	public Map<String, Object> getDataMap(String schemaName, String locale,
			String id) throws DataStoreException {
		Map<String, Object> dataMap = getDataMap(schemaName, id);
		reconcileLocaleFields(dataMap, locale);
		return dataMap;
	}

	public void processDataMap(String schemaName, Map<String, Object> dataMap,
			String locale) throws DataStoreException {

		Schema schema = getObject(Schema.class, schemaName);

		List<Attribute> attributes = schema.getAttributes();

		if (attributes != null) {

			for (Attribute attribute : attributes) {
				if (attribute.isLocaleSpecific()) {
					dataMap.put(attribute.getName() + "_" + locale,
							dataMap.remove(attribute.getName()));
				}

			}

		}

		// Remove Reference Values
		// TODO:DO later

	}

	public String saveData(String schemaName, String locale,
			Map<String, Object> dataMap) throws DataStoreException {
		processDataMap(schemaName, dataMap, locale);
		return saveData(schemaName, dataMap);
	}

	/**
	 * Abstract Methods ( Specific to DataStore Implementations )
	 */
	public abstract String createData(String schemaName,
			Map<String, Object> data) throws DataStoreException;

	public abstract int updateData(String schemaName, Map<String, Object> data)
			throws DataStoreException;

	public abstract Map<String, Object> getDataMap(String schemaName, String id)
			throws DataStoreException;

	public abstract PaginatedResult getDataList(String schemaName,
			Map<String, Object> data, Integer pageNumber, Integer pageSize)
			throws DataStoreException;

	public abstract PaginatedResult getDataList(String schemaName, Query query,
			Integer pageNumber, Integer pageSize) throws DataStoreException;

	public abstract boolean deleteData(String schemaName, String id)
			throws DataStoreException;

	public abstract String getIdColumnName();

	public <T> PaginatedResult<T> getDataList(Class<T> entityClass,
			Query query, String locale, Integer pageNumber, Integer pageSize)
			throws DataStoreException {

		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;

		paginatedResult = getDataList(getSchemaName(entityClass), query,
				pageNumber, pageSize);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(entityClass, map, locale));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult;

	}

	public abstract void createClone(final String applicationName)
			throws DataStoreException;

	public abstract void deleteDataStore(String applicationName)
			throws DataStoreException;

}
