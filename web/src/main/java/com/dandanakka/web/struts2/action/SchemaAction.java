package com.dandanakka.web.struts2.action;

import java.util.ArrayList;
import java.util.List;

import com.dandanakka.datastore.model.Attribute;
import com.dandanakka.datastore.model.Schema;
import com.dandanakka.datastore.model.Application;

public class SchemaAction extends PersistenceAction<Schema> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Schema> schemas;

	public List<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(List<Schema> schemas) {
		this.schemas = schemas;
	}

	private boolean addAttribute;

	public void setAddAttribute(boolean addAttribute) {
		this.addAttribute = true;
	}

	@Override
	public String edit() throws Exception {
		setSchemas(getDataStore().getDataList(new Schema()));
		return super.edit();
	}

	public String submit() throws Exception {
		String result = null;
		if (addAttribute) {
			result = addAttribute();
		} else {
			result = super.submit();
		}
		return result;
	}

	private String addAttribute() throws Exception {
		setSchemas(getDataStore().getDataList(new Schema()));
		List<Attribute> attributes = entity.getAttributes();
		if (attributes == null) {
			attributes = new ArrayList<>(1);
		}
		attributes.add(new Attribute());
		entity.setAttributes(attributes);
		return input();
	}

	@Override
	protected Class<Schema> getEntityClass() {
		// TODO Auto-generated method stub
		return Schema.class;
	}

}
