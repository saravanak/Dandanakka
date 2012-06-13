package com.dandanakka.web.struts2.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dandanakka.datastore.model.Attribute;
import com.dandanakka.datastore.model.Schema;

public class DataAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Submit Actions
	private boolean save;
	private boolean addAttribute;

	public void setSave(boolean save) {
		this.save = true;
	}

	public void setAddAttribute(boolean addAttribute) {
		this.addAttribute = true;
	}

	private Map<String, Object> data;

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	private Schema schema;

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public String edit() throws Exception {
		setSchema(getDataStore().getObject(Schema.class, getParameter("sId")));
		return input();
	}

	public String submit() throws Exception {
		String result = null;
		if (save) {
			result = save();
		} else if (addAttribute) {
			result = addAttribute();
		}
		return result;
	}

	private String save() throws Exception {

		return "save";
	}

	private String addAttribute() throws Exception {
		List<Attribute> attributes = schema.getAttributes();
		if (attributes == null) {
			attributes = new ArrayList<>(1);
		}
		attributes.add(new Attribute());
		schema.setAttributes(attributes);
		return input();
	}

	public String delete() throws Exception {
		getDataStore().deleteData(Schema.class, getParameter("id"));
		return "save";
	}

	public String list() throws Exception {

		return "list";
	}

}
