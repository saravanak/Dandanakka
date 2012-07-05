package com.dandanakka.web.struts2.action;

import com.dandanakka.datastore.model.Application;

public class ApplicationAction extends PersistenceAction<Application> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<Application> getEntityClass() {
		// TODO Auto-generated method stub
		return Application.class;
	}

	protected String save() throws Exception {
		String result = super.save() ;
		getDataStore().createClone(entity.getName());
		return result;
	}
}
