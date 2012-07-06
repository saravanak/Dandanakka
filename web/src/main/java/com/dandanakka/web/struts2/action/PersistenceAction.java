package com.dandanakka.web.struts2.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.PaginatedResult;
import com.dandanakka.web.exception.SystemException;

public abstract class PersistenceAction<T> extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected T entity;

	private Class<T> entityClass;

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public String edit() throws Exception {
		setEntity(getDataStore()
				.getObject(getEntityClass(), getParameter("id"),getLanguage()));
		return input();
	}

	protected String save() throws Exception {
		getDataStore().saveObject(entity,getLanguage(),true);
		loadContext();
		return "save";
	}

	public String submit() throws Exception {
		return save();
	}

	public String delete() throws Exception {
		getDataStore().deleteData(getEntityClass(), getParameter("id"));
		return "save";
	}

	public String list() throws Exception {
		return "list";
	}

	protected PaginatedResult<T> getPaginatedResult(Integer pageNumber,
			Integer pageSize) throws InstantiationException, IllegalAccessException, DataStoreException, SystemException {
		return getDataStore().getDataList(getEntityClass().newInstance(),getLanguage(),
				pageNumber, pageSize);
	}

	public String search() throws DataStoreException, JsonGenerationException,
			JsonMappingException, IOException, InstantiationException,
			IllegalAccessException, SystemException {
		Integer pageNumber = getParameterAsInt("page");
		Integer pageSize = getParameterAsInt("rows");

		PaginatedResult<T> paginatedResult = getPaginatedResult(pageNumber,
				pageSize);

		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		Integer count = paginatedResult.getNoOfRecords();
		map.put("page", pageNumber);
		map.put("records", count);
		map.put("total", (count / pageSize) + 1);
		Map<String, Object> row;
		Map<String, Object> resultMap;
		List<Object> cell;
		for (T resultObj : paginatedResult.getResults()) {
			row = new HashMap<String, Object>();
			resultMap = getDataStore().getDataMap(resultObj);
			row.put("id", resultMap.get(getDataStore().getIdColumn(
					getEntityClass()).getName()));
			cell = new ArrayList<Object>();
			for (String key : resultMap.keySet()) {
				cell.add(resultMap.get(key));
			}

			row.put("cell", cell);
			rows.add(row);
		}
		map.put("rows", rows);
		ObjectMapper mapper = new ObjectMapper();
		return getJSONResponse(mapper.writeValueAsString(map));
	}

	protected abstract Class<T> getEntityClass();

}
