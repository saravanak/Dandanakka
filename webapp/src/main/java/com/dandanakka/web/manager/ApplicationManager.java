package com.dandanakka.web.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dandanakka.datastore.DataStore;
import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.datastore.model.Operator;
import com.dandanakka.datastore.model.Query;
import com.dandanakka.template.model.Link;
import com.dandanakka.template.model.LinkCategory;
import com.dandanakka.web.exception.SystemException;
import com.dandanakka.web.model.Context;

public class ApplicationManager {

	private static final Map<String, ApplicationManager> applicationManagerMap = new HashMap<String, ApplicationManager>();

	private DataStore dataStore;
	private Context context;

	private ApplicationManager(String application) throws SystemException {
		try {
			dataStore = DataStore.getDataStore(application);
			loadContext();
		} catch (Exception e) {
			throw new SystemException("Can not load Data Store", e);
		}

	}

	private void loadContext() throws DataStoreException {
		context = new Context();

		Map<String, List<Link>> links = new HashMap<String, List<Link>>();

		Query query = new Query();
		query.addCriteria("parentId", Operator.IS_NULL, null);
		query.addCriteria("category", Operator.IS_NULL, null);

		links.put("master", getDataStore().getDataList(Link.class, query));

		List<LinkCategory> linkCategories = getDataStore().getDataList(
				new LinkCategory());

		for (LinkCategory linkCategory : linkCategories) {
			query.clearCriterias();
			query.addCriteria("parentId", Operator.IS_NULL, null);
			query.addCriteria("category", Operator.EQUALS,
					linkCategory.getName());
			links.put(linkCategory.getName(),
					getDataStore().getDataList(Link.class, query));
		}
		
		context.setLinks(links) ;
	}

	public static ApplicationManager getApplicationManager(
			final String application) throws SystemException {
		ApplicationManager applicationManager = applicationManagerMap
				.get(application);
		if (applicationManager == null) {
			applicationManager = new ApplicationManager(application);
			applicationManagerMap.put(application, applicationManager);
		}
		return applicationManager;
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public Context getContext() {
		return context;
	}

}
