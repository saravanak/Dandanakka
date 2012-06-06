package com.dandanakka.template.model;

import java.util.List;

import com.dandanakka.datastore.model.Id;
import com.dandanakka.datastore.model.Reference;

public class Link {

	@Id
	private String id;
	private String parentId;
	private String label;
	private String url;
	private String description;
	private String category;

	@Reference(targetAtribute = "parentId", targetSchema = Link.class)
	private List<Link> links;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
