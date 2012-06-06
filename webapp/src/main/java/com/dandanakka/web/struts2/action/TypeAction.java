package com.dandanakka.web.struts2.action;

import com.dandanakka.datastore.model.Type;

public class TypeAction extends PersistenceAction<Type> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<Type> getEntityClass() {
		// TODO Auto-generated method stub
		return Type.class;
	}

}
