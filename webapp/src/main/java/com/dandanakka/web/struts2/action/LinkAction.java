package com.dandanakka.web.struts2.action;

import java.util.List;

import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.Operator;
import com.dandanakka.datastore.model.Query;
import com.dandanakka.template.model.Link;
import com.dandanakka.template.model.LinkCategory;
import com.dandanakka.web.exception.SystemException;

public class LinkAction extends PersistenceAction<Link> {

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

	private LinkCategory linkCategory;

	public LinkCategory getLinkCategory() {
		return linkCategory;
	}

	public void setLinkCategory(LinkCategory category) {
		this.linkCategory = category;
	}

	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String add() {
		return "edit";
	}

	@Override
	protected String save() throws Exception {
		String returnValue = null;
		if (linkCategory == null) {
			if (entity.getParentId().trim().length() == 0) {
				entity.setParentId(null);
			}
			if (entity.getCategory().trim().length() == 0) {
				entity.setCategory(null);
			}
			returnValue = super.save();
			;
		} else {
			getDataStore().saveData(linkCategory);
			returnValue = "save";
		}

		return returnValue;
	}

	public String edit() throws Exception {
		setEntity(getDataStore()
				.getObject(getEntityClass(), getParameter("id")));

		if (entity == null) {
			String parentId = getParameter("parentId");
			setEntity(new Link());
			getEntity().setParentId(parentId);
			getEntity().setCategory(category);

		}
		return input();
	}

	public String list() throws Exception {
		addMaster(LinkCategory.class);
		String category = getParameter("category");
		Query query = new Query();
		query.addCriteria("parentId", Operator.IS_NULL, null);
		if (category == null) {
			query.addCriteria("category", Operator.IS_NULL, null);
		} else {
			query.addCriteria("category", Operator.EQUALS, category);
		}
		setLinks(getDataStore().getDataList(getEntityClass(), query));
		return "list";
	}

	public String actions() throws Exception {
		return "actions";
	}

	public String category() throws Exception {
		setLinkCategory(getDataStore().getObject(LinkCategory.class,
				getParameter("id")));
		return "category";
	}

	public String delete() throws Exception {
		deleteLink(getDataStore().getObject(Link.class, getParameter("id")));

		return "save";
	}

	private void deleteLink(Link link) throws DataStoreException,
			SystemException {
		List<Link> links = link.getLinks();
		for (Link link2 : links) {
			deleteLink(link2);
		}
		getDataStore().deleteData(Link.class, link.getId());
	}

	@Override
	protected Class<Link> getEntityClass() {
		return Link.class;
	}

}
