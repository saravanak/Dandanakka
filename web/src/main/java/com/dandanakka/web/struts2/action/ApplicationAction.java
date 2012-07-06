package com.dandanakka.web.struts2.action;

import java.util.List;

import com.dandanakka.datastore.model.Application;
import com.dandanakka.template.model.Template;

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
		String result = super.save();
		getDataStore().createClone(entity.getName());
		return result;
	}

	@Override
	public String delete() throws Exception {
		String result = super.delete();
		getDataStore().deleteDataStore(getParameter("id"));
		return result;
	}

	@Override
	public String edit() throws Exception {
		addMaster(Template.class);
		return super.edit();
	}
}
