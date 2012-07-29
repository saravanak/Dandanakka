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
import com.dandanakka.template.model.Template;
import com.dandanakka.web.exception.SystemException;

public class DataAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Map<String, Object> entity;

	private String template;
	private String link;
	private String dataId;
	private String page;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	private Object mapObj;

	public Object getMapObj() {
		return mapObj;
	}

	public void setMapObj(Object mapObj) {
		this.mapObj = mapObj;
	}

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
		return getDataStore().getIdColumnName();
	}

	public void setEntity(Map<String, Object> entity) {
		this.entity = entity;
	}

	public String edit() throws Exception {
		String schemaName = null;
		String dataId = getParameter("id");
		setTemplate(getParameter("tId"));
		setLink(getParameter("lId"));
		setPage(getParameter("pId"));
		if (template != null) {
			Template templateObj = getDataStore().getObject(Template.class,
					template);
			if (templateObj != null) {
				schemaName = templateObj.getSchema();
			}
		} else {
			schemaName = getParameter("sId");
		}
		setSchema(getDataStore().getObject(Schema.class, schemaName));
		if (dataId != null) {
			setEntity(getDataStore().getDataMap(schemaName, dataId));
		}

		return input();
	}

	protected String save() throws Exception {
		processEntity(entity);
		setDataId(getDataStore().saveData(getSchema().getName(),getLanguage(), entity));
		if (link != null && link.trim().length() != 0) {
			return "page";
		}
		else if (page != null && page.trim().length() != 0) {
			return "viewpage";
		}
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
				} else {
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

	private void processEntity(Map<String, Object> map) {
		if (map != null) {
			Object value = null;
			for (String key : map.keySet()) {
				value = map.get(key);
				if (isArray(value)) {
					map.put(key, getConvertedValue(key, ((Object[]) value)[0]));
				}
			}
		}
	}

	private Object getConvertedValue(String key, Object object) {
		// TODO Auto-generated method stub
		return object.toString();
	}

	private boolean isArray(final Object obj) {
		return obj instanceof Object[] || obj instanceof boolean[]
				|| obj instanceof byte[] || obj instanceof short[]
				|| obj instanceof char[] || obj instanceof int[]
				|| obj instanceof long[] || obj instanceof float[]
				|| obj instanceof double[];
	}

}
