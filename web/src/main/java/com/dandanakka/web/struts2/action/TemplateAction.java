package com.dandanakka.web.struts2.action;

import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.Application;
import com.dandanakka.datastore.model.Operator;
import com.dandanakka.datastore.model.PaginatedResult;
import com.dandanakka.datastore.model.Query;
import com.dandanakka.datastore.model.Schema;
import com.dandanakka.template.model.LinkCategory;
import com.dandanakka.template.model.Template;
import com.dandanakka.template.model.TemplateCategory;
import com.dandanakka.web.exception.SystemException;

public class TemplateAction extends PersistenceAction<Template> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private TemplateCategory templateCategory;

	public TemplateCategory getTemplateCategory() {
		return templateCategory;
	}

	public void setTemplateCategory(TemplateCategory templateCategory) {
		this.templateCategory = templateCategory;
	}

	@Override
	protected Class<Template> getEntityClass() {
		return Template.class;
	}

	public String list() throws Exception {
		addMaster(TemplateCategory.class);
		return super.list();
	}

	public String select() throws Exception {
		return "select";
	}
	
	public String theme() throws Exception {
		Application application = new Application() ;
		application.setTheme(getParameter("tId")) ;
		getDataStore().saveApplication(application) ;
		loadContext() ;
		return "theme";
	}

	public String edit() throws Exception {
		addMaster(Schema.class);
		setEntity(getDataStore()
				.getObject(getEntityClass(), getParameter("id")));

		if (entity == null) {

			setEntity(new Template());

			getEntity().setCategory(category);

		}
		return input();
	}

	@Override
	protected String save() throws Exception {
		String returnValue = null;
		if (templateCategory == null) {
			returnValue = super.save();
		} else {
			getDataStore().saveObject(templateCategory);
			returnValue = "save";
		}
		return returnValue;
	}

	public String category() throws Exception {
		setTemplateCategory(getDataStore().getObject(TemplateCategory.class,
				getParameter("id")));
		return "category";
	}

	protected PaginatedResult<Template> getPaginatedResult(Integer pageNumber,
			Integer pageSize) throws InstantiationException,
			IllegalAccessException, DataStoreException, SystemException {

		Query query = new Query();

		if (category == null) {
			query.addCriteria("category", Operator.IS_NULL, null);
		} else {
			query.addCriteria("category", Operator.EQUALS, category);
		}

		return getDataStore().getDataList(getEntityClass(), query,getLanguage(), pageNumber,
				pageSize);
	}
}
