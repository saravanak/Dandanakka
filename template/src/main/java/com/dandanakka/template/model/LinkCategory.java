package com.dandanakka.template.model;

import com.dandanakka.datastore.model.Id;

public class LinkCategory {
	@Id(auto = false)
	private String name;

	private String label;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
