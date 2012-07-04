package com.dandanakka.web.struts2.freemarker;

import com.dandanakka.datastore.DataStore;
import com.dandanakka.datastore.exception.DataStoreException;
import com.dandanakka.template.model.Template;

import freemarker.cache.StringTemplateLoader;

public class TemplateLoader extends StringTemplateLoader {
	@Override
	public Object findTemplateSource(String name) {
		Object source = super.findTemplateSource(name) ;		
		if(name.indexOf("$$") != -1 && source == null ) {
			int lastIndexofDoller = name.lastIndexOf("$$") ;
			try {
				Template template = DataStore.getDataStore(name.substring(name.indexOf("$$")+2,lastIndexofDoller)).getObject(Template.class, name.substring(lastIndexofDoller+3)) ;
				if(template != null) {
					this.putTemplate(name, template.getTemplate()) ;
				}
				source =  super.findTemplateSource(name);	
			} catch (DataStoreException e) {				
				e.printStackTrace();
			}
			
		}
		
		return source;
	}
	
	
}
