package com.pivotal.cf.broker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class EntryUtil {
	private static final Logger logger = Logger.getLogger(EntryUtil.class);
	
	public static Object getObjectByFormOld(HttpServletRequest request, Class<?> clazz) {
		Object result = null;
		try {
			result = clazz.newInstance();
			String value;
			for(Field field : clazz.getDeclaredFields()) {
				value = request.getParameter(field.getName());
				
				if(value == null || "".equals(value)) {
					continue;
				} else {
					value = value.trim();
				}
					
				if("Long".equals(field.getType().getSimpleName())) {
					setValue(result, field.getName(), Long.valueOf(value));
				} else if("String".equals(field.getType().getSimpleName())) {
					setValue(result, field.getName(), value);
				} else if("Integer".equals(field.getType().getSimpleName())) {
					setValue(result, field.getName(), Integer.valueOf(value));
				} else if("Date".equals(field.getType().getSimpleName())) {
					setValue(result, field.getName(), DateUtil.parseDate(value));
				} else if("Float".equals(field.getType().getSimpleName())) {
					setValue(result, field.getName(), Float.valueOf(value));
				} else {
					logger.error("Please add Type here!");
				}
			}
		} catch (Exception e) {
			LogUtil.exception(logger, e);
			result = new Object();
		}
		
		return result;
	}
	
	public static Object getObjectByForm(HttpServletRequest request, Class<?> clazz) {
		Object result = null;
		try {
			result = clazz.newInstance();
			getObjectByForm(request, clazz, result);
		} catch (Exception e) {
			LogUtil.exception(logger, e);
			result = new Object();
		}
		
		return result;
	}
	
	private static void getObjectByForm(HttpServletRequest request, Class<?> clazz, Object object) {
		
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			getObjectByForm(request, superclass, object);
		}
		
		try {
			String value;
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				value = request.getParameter(field.getName());

				if (value == null || "".equals(value)) {
					continue;
				} else {
					value = value.trim();
				}

				if ("Long".equals(field.getType().getSimpleName())) {
					setValue(clazz, object, field.getName(), Long.valueOf(value));
				} else if ("String".equals(field.getType().getSimpleName())) {
					setValue(clazz, object, field.getName(), value);
				} else if ("Integer".equals(field.getType().getSimpleName())) {
					setValue(clazz, object, field.getName(), Integer.valueOf(value));
				} else if ("Date".equals(field.getType().getSimpleName())) {
					setValue(clazz, object, field.getName(), DateUtil.parseDate(value));
                } else if ("Float".equals(field.getType().getSimpleName())) {
                    setValue(clazz, object, field.getName(), Float.valueOf(value));
                } else if ("Double".equals(field.getType().getSimpleName())) {
                    setValue(clazz, object, field.getName(), Double.valueOf(value));
				} else {
					logger.error("Please add Type here! Type name is " + field.getType().getSimpleName());
				}
			}
		} catch (Exception e) {
			LogUtil.exception(logger, e);
		}
	}

    public static Object getObjectByJson(HttpServletRequest request, Class<?> clazz) {
        String jsonStr = getJsonStr(request);
        return JsonUtil.toObject(jsonStr, clazz);
    }

    public static Object getListByJson(HttpServletRequest request, Class<?> clazz) {
        String jsonStr = getJsonStr(request);
        return JsonUtil.toList(jsonStr, clazz);
    }
	
	public static Object getObjectByJson(HttpServletRequest request, Class<?> clazz, Map<String, Class<?>> classMap) {
        String jsonStr = getJsonStr(request);
        return JsonUtil.toObject(jsonStr, clazz, classMap);
	}
	
	public static String getJsonStr(HttpServletRequest request) {
		StringBuffer jsonStr = new StringBuffer();  
		String line = null; 
		try {
			BufferedReader reader = request.getReader();  
			while((line = reader.readLine()) != null) {  
				jsonStr.append(line);  
			}
		} catch (IOException e) {
			LogUtil.exception(logger, e);
		}
		
		String result = jsonStr.toString();
		if(result == null || "".equals(result)) {
			result = "{}";
		}
		
		logger.info("Json string from request.length():" + result.length());
		if(result.length() > 256) {
			logger.info("Json string from request:" + result.substring(0, 256) + "...");
		} else {
			logger.info("Json string from request:" + result);
		}
		return result;
	}
	
	private static void setValue(Class<?> clazz, Object obj, String column, Object value) {
		String method = "set" + column.substring(0, 1).toUpperCase() + column.substring(1);
		try {
			Method setter = clazz.getDeclaredMethod(method, value.getClass());
			setter.invoke(obj, value);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private static void setValue(Object obj, String column, Object value) {
		String method = "set" + column.substring(0, 1).toUpperCase() + column.substring(1);
		try {
			Method setter = obj.getClass().getDeclaredMethod(method, value.getClass());
			setter.invoke(obj, value);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
