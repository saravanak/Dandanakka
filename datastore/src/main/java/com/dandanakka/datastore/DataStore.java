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
import com.dandanakka.datastore.model.Id;
import com.dandanakka.datastore.model.Operator;
import com.dandanakka.datastore.model.PaginatedResult;
import com.dandanakka.datastore.model.Query;
import com.dandanakka.datastore.model.Reference;

public abstract class DataStore {

	private static final Map<String, DataStore> dataStoreMap = new HashMap<String, DataStore>();

	private String application;

	protected String getApplication() {
		return application;
	}

	protected void setApplication(String application) {
		this.application = application;
	}

	public static DataStore getDataStore(final String application)
			throws DataStoreException {
		DataStore dataStore = dataStoreMap.get(application);
		if (dataStore == null) {
			try {
				dataStore = (DataStore) Class.forName(
						ConfigUtil.getCongiguration("datastore")).newInstance();
				dataStore.setApplication(application);
				dataStoreMap.put(application, dataStore);
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
		T obj = null;
		Map<String, Object> dataMap = getDataMap(getSchemaName(clazz), id);
		obj = getObject(clazz, dataMap);
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
		Map<String, Object> dataMap = null;

		ObjectMapper m = new ObjectMapper();
		dataMap = m.convertValue(data, Map.class);

		return dataMap;
	}

	private <T> T getObject(Class<T> clazz, Map<String, Object> dataMap)
			throws DataStoreException {
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
						getDataList(reference.targetSchema(), query));
			}

			obj = m.convertValue(dataMap, clazz);
		}
		return obj;
	}

	public List<Field> getReferenceColumns(Class clazz) {
		List<Field> fields = new ArrayList<>();
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
		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;

		paginatedResult = getDataList(getSchemaName(clazz), query, null, null);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(clazz, map));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult == null ? null : paginatedResult.getResults();
	}

	public <T> List<T> getDataList(T data) throws DataStoreException {
		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;
		Map<String, Object> query = getDataMap(data);
		removeNullValues(query);

		paginatedResult = getDataList(getSchemaName(data.getClass()), query,
				null, null);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(data.getClass(), map));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult == null ? null : paginatedResult.getResults();
	}

	public <T> PaginatedResult<T> getDataList(T data, int pageNumber,
			int pageSize) throws DataStoreException {

		PaginatedResult<T> paginatedResult = null;
		List<T> list = null;
		Map<String, Object> query = getDataMap(data);
		removeNullValues(query);

		paginatedResult = getDataList(getSchemaName(data.getClass()), query,
				pageNumber, pageSize);
		if (paginatedResult != null) {
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) paginatedResult
					.getResults();

			if (mapList != null) {
				list = new ArrayList<T>(mapList.size());
				for (Map<String, Object> map : mapList) {
					list.add((T) getObject(data.getClass(), map));
				}
			}
			paginatedResult.setResults(list);
		}

		return paginatedResult;
	}

	public void saveData(String schemaName, Map<String, Object> dataMap)
			throws DataStoreException {

		String idName = getIdColumnName();
		Object idValue = dataMap.get(idName);
		dataMap.remove(idName);

		if (idValue == null || idValue.toString().trim().length() == 0) {
			createData(schemaName, dataMap);
		} else {
			dataMap.put(getIdColumnName(), idValue);
			updateData(schemaName, dataMap);
		}

	}

	public void saveData(Object data) throws DataStoreException {
		if (data != null) {
			Map<String, Object> dataMap = getDataMap(data);
			Field idField = getIdColumn(data.getClass());
			String idName = idField.getName();
			Object idValue = dataMap.get(idName);
			dataMap.remove(idName);
			if (idField.getAnnotation(Id.class).auto()) {
				if (idValue == null || idValue.toString().trim().length() == 0) {
					createData(getSchemaName(data.getClass()), dataMap);
				} else {
					dataMap.put(getIdColumnName(), idValue);
					updateData(getSchemaName(data.getClass()), dataMap);
				}
			} else {
				dataMap.put(getIdColumnName(), idValue);
				if (updateData(getSchemaName(data.getClass()), dataMap) == 0) {
					createData(getSchemaName(data.getClass()), dataMap);
				}

			}
		}
	}

	public boolean deleteData(Class schemaClass, String id)
			throws DataStoreException {
		return deleteData(getSchemaName(schemaClass), id);
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
			Query query, Integer pageNumber, Integer pageSize)
			throws DataStoreException {
		return getDataList(getSchemaName(entityClass), query, pageNumber,
				pageSize);
	}
}
