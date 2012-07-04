package com.dandanakka.web.struts2.freemarker;

import javax.servlet.ServletContext;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

public class FreemarkerManager extends
		org.apache.struts2.views.freemarker.FreemarkerManager {

	@Override
	protected TemplateLoader createTemplateLoader(ServletContext arg0,
			String arg1) {
		TemplateLoader templateLoader = new MultiTemplateLoader(
				new TemplateLoader[] {
						new com.dandanakka.web.struts2.freemarker.TemplateLoader(),
						super.createTemplateLoader(arg0, arg1)
						 });
		return templateLoader;
	}

}
