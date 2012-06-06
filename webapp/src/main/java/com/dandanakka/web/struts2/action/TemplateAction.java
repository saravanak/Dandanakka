package com.dandanakka.web.struts2.action;

import com.dandanakka.template.model.Template;

public class TemplateAction extends PersistenceAction<Template> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<Template> getEntityClass() {
		
		return Template.class;
	}

}
