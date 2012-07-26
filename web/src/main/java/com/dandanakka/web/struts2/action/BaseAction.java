package com.dandanakka.web.struts2.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.dandanakka.datastore.DataStore;
import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.web.exception.SystemException;
import com.dandanakka.web.manager.ApplicationManager;
import com.dandanakka.web.model.Context;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ParameterAware,
		ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String application;

	private ApplicationManager applicationManager;

	private Map<String, String[]> params;

	protected HttpServletResponse response;

	private ApplicationManager getApplicationManager() throws SystemException {
		if (applicationManager == null) {
			applicationManager = ApplicationManager
					.getApplicationManager(getApplication());
		}
		return applicationManager;
	}

	protected DataStore getDataStore() throws SystemException {
		return getApplicationManager().getDataStore();
	}

	public Context getContext() throws SystemException {
		return getApplicationManager().getContext(getLanguage());
	}

	public void loadContext() throws SystemException, DataStoreException {
		getApplicationManager().loadContext();
	}

	public String getTheme() {
		return "basic";
	}

	public String getApplication() {
		if (application == null) {
			String nameOfAcion = ActionContext.getContext().getName();
			String[] pathelements = nameOfAcion.split("/");
			application = pathelements[0];
		}
		return application;
	}

	@Override
	public void setParameters(Map<String, String[]> params) {
		this.params = params;
	}

	protected String[] getParameterValues(String key) {
		return params.get(key);
	}

	protected String getParameter(String key) {
		String[] values = getParameterValues(key);
		return values == null ? null : params.get(key)[0];
	}

	protected Integer getParameterAsInt(String key) {
		String value = getParameter(key);
		return value == null ? null : Integer.parseInt(value);

	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	protected String getJSONResponse(String content) throws IOException {
		response.setContentType("json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(content);
		writer.close();
		return null;
	}

	protected void addMaster(Class clazz) throws InstantiationException,
			IllegalAccessException, DataStoreException, SystemException {

		ActionContext
				.getContext()
				.getValueStack()
				.set(getPlural(clazz),
						getDataStore().getDataList(clazz.newInstance()));
	}

	private String getName(Class clazz) {
		String name = clazz.getName();
		int indexOfDot = -1;
		if ((indexOfDot = name.lastIndexOf('.')) != -1) {
			name = name.substring(indexOfDot + 1);
		}
		return name;
	}

	private String getPlural(Class clazz) {
		String name = getName(clazz) ;
		if(name.endsWith("y") && !name.endsWith("ay") && !name.endsWith("ey") && !name.endsWith("iy") && !name.endsWith("oy") && !name.endsWith("uy")) {
			name = name.substring(0,name.lastIndexOf('y')) + "ies";
		}
		else if(name.endsWith("x") || name.endsWith("h")) {
			name = name + "es";
		}
		else {
			name = name + "s";
		}
		return name;
	}

	protected String getLanguage() {
		String language = getLocale().getLanguage();
		return language.indexOf("en") == -1 ? language : null;
	}

}
