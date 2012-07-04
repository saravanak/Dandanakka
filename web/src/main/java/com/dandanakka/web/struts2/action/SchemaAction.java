package com.dandanakka.web.struts2.action;

import java.util.ArrayList;
import java.util.List;

import com.dandanakka.datastore.model.Attribute;
import com.dandanakka.datastore.model.Schema;
import com.dandanakka.datastore.model.Type;

public class SchemaAction extends PersistenceAction<Schema> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private List<Type> types;

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	private boolean addAttribute;

	public void setAddAttribute(boolean addAttribute) {
		this.addAttribute = true;
	}

	@Override
	public String edit() throws Exception {
		setTypes(getDataStore().getDataList(new Type()));
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
		setTypes(getDataStore().getDataList(new Type()));
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
