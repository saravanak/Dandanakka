package com.dandanakka.template.model;

import com.dandanakka.datastore.model.Id;

public class Locale {
	
	@Id(auto=false)
	private String code;
	
	private String label;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}