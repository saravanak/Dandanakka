package com.dandanakka.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigUtil {

	private static Properties configs;

	private static Properties getConfigurations() throws IOException {
		if (configs == null) {
			configs = new Properties();
			InputStream inputStream = ConfigUtil.class
					.getResourceAsStream("/config.properties");
			
				configs.load(inputStream);
				inputStream.close();
			

		}
		return configs;
	}

	public static String getCongiguration(String key) throws IOException  {
		return getConfigurations().getProperty(key);
	}
	
	public static Integer getCongigurationAsInt(String key) throws IOException  {
		Integer value = null ;
		String valueStr = getCongiguration(key) ;
		if(valueStr != null && valueStr.trim().length() != 0) {
			value = Integer.parseInt(valueStr) ;
		}
		return value;
	}
}
