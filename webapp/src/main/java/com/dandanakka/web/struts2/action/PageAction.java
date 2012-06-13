package com.dandanakka.web.struts2.action;

import com.dandanakka.template.model.Page;

public class PageAction extends PersistenceAction<Page> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<Page> getEntityClass() {
		// TODO Auto-generated method stub
		return Page.class;
	}

}
