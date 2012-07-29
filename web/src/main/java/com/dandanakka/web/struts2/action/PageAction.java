package com.dandanakka.web.struts2.action;

import java.util.Map;

import com.dandanakka.template.model.Link;
import com.dandanakka.template.model.Page;
import com.dandanakka.template.model.Template;

public class PageAction extends PersistenceAction<Page> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String template;
	private String link;
	private String dataId;

	private String content;

	private Map<String, Object> data;

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		if (entity == null) {
			entity = new Page();
			entity.setDataId(getParameter("dataId"));
			entity.setTemplateName(getParameter("template"));
			setLink(getParameter("link"));
		}
		return result;
	}

	@Override
	protected String save() throws Exception {
		String result = super.save();
		if (link != null && link.trim().length() != 0) {
			Link linkToUpdate = new Link();
			linkToUpdate.setId(link);
			linkToUpdate.setPage(entity.getName());
			getDataStore().saveObject(linkToUpdate, true);
			loadContext();
			
		}
		return result;
	}
	
	public String link() throws Exception {
		setLink(getParameter("link"));
		if (link != null && link.trim().length() != 0) {
			Link linkToUpdate = new Link();
			linkToUpdate.setId(link);
			linkToUpdate.setPage(getParameter("pName"));
			getDataStore().saveObject(linkToUpdate, true);
			loadContext();
		}
		return view() ;
	}
	
	@Override
	public String list() throws Exception {
		setLink(getParameter("link"));
		return super.list();
	}

	public String view() throws Exception {
		setEntity(getDataStore().getObject(Page.class, getParameter("pName")));
		setData(getDataStore().getDataMap(
				getDataStore().getObject(Template.class,
						entity.getTemplateName()).getSchema(),getLanguage(),
				entity.getDataId()));
		return "view";
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	@Override
	protected Class<Page> getEntityClass() {
		return Page.class;
	}

}
