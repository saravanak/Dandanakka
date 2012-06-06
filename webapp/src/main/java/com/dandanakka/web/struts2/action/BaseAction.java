package com.dandanakka.web.struts2.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.dandanakka.datastore.DataStore;
import com.dandanakka.datastore.exception.DataStoreException;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseAction extends ActionSupport implements ParameterAware,
		ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String application;

	private DataStore dataStore;

	private Map<String, String[]> params;

	protected HttpServletResponse response;

	protected DataStore getDataStore() throws DataStoreException {
		if (dataStore == null) {
			dataStore = DataStore.getDataStore(getApplication());
		}
		return dataStore;
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
		PrintWriter writer = response.getWriter();
		writer.write(content);
		writer.close();
		return null;
	}

}
