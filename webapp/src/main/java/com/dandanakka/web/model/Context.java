package com.dandanakka.web.model;

import java.util.List;
import java.util.Map;

import com.dandanakka.template.model.Link;

public class Context {

	private Map<String, List<Link>> links;

	public Map<String, List<Link>> getLinks() {
		return links;
	}

	public void setLinks(Map<String, List<Link>> links) {
		this.links = links;
	}

}
