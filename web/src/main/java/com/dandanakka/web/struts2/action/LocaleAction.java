package com.dandanakka.web.struts2.action;

import com.dandanakka.template.model.Link;
import com.dandanakka.template.model.Locale;

public class LocaleAction extends PersistenceAction<Locale> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Class<Locale> getEntityClass() {
		// TODO Auto-generated method stub
		return Locale.class;
	}

	public java.util.Locale[] getAvailableLocales() {
		return java.util.Locale.getAvailableLocales();
	}

	@Override
	protected String save() throws Exception {
		String result = super.save();
		getDataStore().translate(entity.getCode(), Link.class);
		loadContext();
		return result;
	}

}
