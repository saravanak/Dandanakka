package com.dandanakka.web.struts2.action;

import com.dandanakka.datastore.model.Schema;
import com.dandanakka.web.model.Setting;

public class SettingAction extends PersistenceAction<Setting> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String edit() throws Exception {
		addMaster(Schema.class);
		return super.edit();
	}

	@Override
	protected Class<Setting> getEntityClass() {
		// TODO Auto-generated method stub
		return Setting.class;
	}

}
