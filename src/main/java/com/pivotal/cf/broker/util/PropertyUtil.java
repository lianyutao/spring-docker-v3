package com.pivotal.cf.broker.util;

import java.util.Properties;

public final class PropertyUtil {
//	private static Logger logger = Logger.getLogger(PropertyUtil.class);
	private final static String CONF_DIR = "jdbc.properties";
	private static Properties pro;

	private PropertyUtil() {
	}

	public static String getProperty(String name) {

		if (pro != null)
			return pro.getProperty(name);
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl == null) {
				cl = PropertyUtil.class.getClassLoader();
			}
			pro = new Properties();
			pro.load(cl.getResourceAsStream(CONF_DIR));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pro.getProperty(name);
	}
}
