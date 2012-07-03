package com.dandanakka.web.model;

import java.util.List;
import java.util.Map;

import com.dandanakka.template.model.Link;
import com.dandanakka.template.model.Locale;

public class Context {

	private List<Locale> locales;

	private Map<String, List<Link>> links;

	public Map<String, List<Link>> getLinks() {
		return links;
	}

	public void setLinks(Map<String, List<Link>> links) {
		this.links = links;
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public void setLocales(List<Locale> locales) {
		this.locales = locales;
	}

}
