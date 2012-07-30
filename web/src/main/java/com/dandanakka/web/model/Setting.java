package com.dandanakka.web.model;

import java.util.Map;

import com.dandanakka.datastore.model.Id;

public class Setting {
	@Id(auto = false)
	private String name;
	private String label;
	private String schemaName;
	private Map<String, Object> data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	

}
