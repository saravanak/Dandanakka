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
import com.dandanakka.datastore.model.Schema;
import com.dandanakka.web.exception.SystemException;

public class DataAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Map<String, Object> entity;

	private Schema schema;

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Map<String, Object> getEntity() {
		return entity;
	}
	
	public String getIdColumnName() throws SystemException {
		return getDataStore().getIdColumnName() ;
	}

	public void setEntity(Map<String, Object> entity) {
		this.entity = entity;
	}

	public String edit() throws Exception {
		setSchema(getDataStore().getObject(Schema.class, getParameter("sId")));
		setEntity(getDataStore().getDataMap(getParameter("sId"),
				getParameter("id")));
		return input();
	}

	protected String save() throws Exception {
		getDataStore().saveData(getSchema().getName(), entity);
		return "save";
	}

	public String submit() throws Exception {
		return save();
	}

	public String delete() throws Exception {
		getDataStore().deleteData(getParameter("sId"), getParameter("id"));
		return "save";
	}

	public String list() throws Exception {
		setSchema(getDataStore().getObject(Schema.class, getParameter("sId")));
		return "list";
	}

	protected PaginatedResult getPaginatedResult(Integer pageNumber,
			Integer pageSize) throws InstantiationException,
			IllegalAccessException, DataStoreException, SystemException {
		return getDataStore().getDataList(getParameter("sId"),
				new HashMap<String, Object>(), pageNumber, pageSize);
	}

	public String search() throws DataStoreException, JsonGenerationException,
			JsonMappingException, IOException, InstantiationException,
			IllegalAccessException, SystemException {
		Integer pageNumber = getParameterAsInt("page");
		Integer pageSize = getParameterAsInt("rows");

		PaginatedResult paginatedResult = getPaginatedResult(pageNumber,
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
		String idColumnName = getDataStore().getIdColumnName();
		for (Object resultObj : paginatedResult.getResults()) {
			row = new HashMap<String, Object>();
			resultMap = (Map<String, Object>) resultObj;
			row.put("id", resultMap.get(idColumnName).toString());
			cell = new ArrayList<Object>();
			for (String key : resultMap.keySet()) {
				if (key.equals(idColumnName)) {
					cell.add(resultMap.get(key).toString());
				}
				else {
					cell.add(resultMap.get(key));
				}

			}

			row.put("cell", cell);
			rows.add(row);
		}
		map.put("rows", rows);
		ObjectMapper mapper = new ObjectMapper();
		return getJSONResponse(mapper.writeValueAsString(map));
	}
}
