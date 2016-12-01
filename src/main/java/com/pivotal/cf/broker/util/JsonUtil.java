package com.pivotal.cf.broker.util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertySetStrategy;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);
    private static JsonConfig jsonConfig = new JsonConfig();
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);

    static {
        jsonConfig();
        dateConfig(DEFAULT_DATE_PATTERN);
        timestampConfig(DEFAULT_DATE_PATTERN);
        jsonConfig.setAllowNonStringKeys(true);  
    }
    
    public static String toJson(Object obj) {
        String jsonString = null;

        if (obj != null) {
            if (obj instanceof Collection || obj instanceof Object[]) {
                jsonString = JSONArray.fromObject(obj, jsonConfig).toString();
            } else {
                jsonString = JSONObject.fromObject(obj, jsonConfig).toString();
            }
        }
        return jsonString;
    }

    public static Object toObject(String jsonStr, Class<?> clazz) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
        } catch (Exception e) {
            logger.warn(e);
        }
        jsonConfig.setRootClass(clazz);
        return JSONObject.toBean(jsonObject, jsonConfig);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getMapFromJson(String jsonString) {
        if(jsonString == null || jsonString.length() == 0) {
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Map map = new HashMap();
        for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
            String key = (String) iter.next();
            if(jsonObject.get(key) instanceof JSONArray) {
                map.put(key, toList(toJson(jsonObject.get(key)), Map.class));
            } else if(jsonObject.get(key) instanceof JSONObject) {
                map.put(key, getMapFromJson(toJson(jsonObject.get(key))));
            } else {
                map.put(key, jsonObject.get(key));
            }
        }
        return map;
    }
    public static Object toResult(String jsonStr, Class<?> clazz, Class<?> columnClazz) {
        Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
        classMap.put("result", columnClazz);
        return toObject(jsonStr, clazz, classMap);
    }

    public static Object toList(String jsonStr, Class<?> clazz, Class<?> columnClazz) {
        Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
        classMap.put("list", columnClazz);
        return toObject(jsonStr, clazz, classMap);
    }

    public static Object toObject(String jsonStr, Class<?> clazz, String column, Class<?> columnClazz) {
        Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
        classMap.put(column, columnClazz);
        return toObject(jsonStr, clazz, classMap);
    }

    public static Object toObject(String jsonStr, Class<?> clazz, Map<String, Class<?>> classMap) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
        } catch (Exception e) {
            logger.warn(e);
        }
        return JSONObject.toBean(jsonObject, clazz, classMap);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<?> toList(String jsonStr, Class<?> clazz) {
        List<Object> list = new ArrayList<Object>();
        try {
            JSONArray array = JSONArray.fromObject(jsonStr);
            if("Map".equals(clazz.getSimpleName())) {
                Map map;
                for (Object obj : array.toArray()) {
                    map = new HashMap();
                    if(obj instanceof JSONObject) {
                        for (Iterator iter = ((JSONObject) obj).keys(); iter.hasNext();) {
                            String key = (String) iter.next();
                            if(((JSONObject) obj).get(key) instanceof JSONObject) {
                                map.put(key, getMapFromJson(toJson(((JSONObject) obj).get(key))));
                            } else  {
                                map.put(key, ((JSONObject) obj).get(key));
                            }
                        }
                        list.add(map);
                    } else {
                        list.add(obj);
                    }
                }
            } else {
                jsonConfig.setRootClass(clazz);
                for (Object obj : array.toArray()) {
                    list.add(JSONObject.toBean((JSONObject) obj, jsonConfig));
                }
            }
        } catch (Exception e) {
            logger.warn(e);
        }
        return list;
    }

    public static void setDateFormat(String datePattern) {
        try {
            dateFormat = new SimpleDateFormat(datePattern);
        } catch (Exception e) {
            dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        }
        dateConfig(datePattern);
        timestampConfig(datePattern);
        jsonConfig();
        
    }
    
    public static void setTimeZone(TimeZone tz) {
        dateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        dateFormat.setTimeZone(tz);
        dateConfig(DEFAULT_DATE_PATTERN);
        jsonConfig();
    }

    private static void dateConfig(String pattern) {
        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] { pattern }), true);
    }

    private static void timestampConfig(String pattern) {
        JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(new String[] { pattern }), true);
    }

    private static void jsonConfig() {

        jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonValueProcessor() {
            public Object processArrayValue(Object value, JsonConfig config) {
                return process(value);
            }

            public Object processObjectValue(String arg0, Object value, JsonConfig arg2) {
                return process(value);
            }

            private Object process(Object value) {
                try {

                    if (value instanceof java.sql.Timestamp) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return sdf.format(value);
                    }
                    return value == null ? null : value.toString();
                } catch (Exception e) {
                    logger.error(e);
                    return "";
                }

            }
        });

        jsonConfig.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return process(value);
            }

            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                return process(value);
            }

            private Object process(Object value) {
                Object obj = null;
                if (value != null) {
                    obj = dateFormat.format((Date) value);
                }
                return obj;
            }
        });

        jsonConfig.registerJsonValueProcessor(String.class, new JsonValueProcessor() {
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return process(value);
            }

            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                return process(value);
            }

            private Object process(Object value) {
                Object obj = null;
                if (value != null) {
                    obj = ((String) value).trim();
                }
                return obj;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Date.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Timestamp.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(String.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Long.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Float.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Double.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });

        jsonConfig.registerDefaultValueProcessor(Boolean.class, new DefaultValueProcessor() {
            @SuppressWarnings("rawtypes")
            public Object getDefaultValue(Class type) {
                return null;
            }
        });
        jsonConfig.setPropertySetStrategy(new PropertySetStrategy() {

            @Override
            public void setProperty(Object bean, String key, Object value) throws JSONException {
                setProperty(bean, key, value, new JsonConfig());
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void setProperty(Object bean, String key, Object value, JsonConfig jsonConfig) throws JSONException {
                if (bean instanceof Map) {
                    ((Map) bean).put(key, value);
                } else {
                    if (!jsonConfig.isIgnorePublicFields()) {
                        try {
                            Field field = bean.getClass().getField(key);
                            if (field != null)
                                field.set(bean, value);
                        } catch (Exception e) {
                            _setProperty(bean, key, value);
                        }
                    } else {
                        _setProperty(bean, key, value);
                    }
                }
            }

            private void _setProperty(Object bean, String key, Object value) {
                try {
                    PropertyUtils.setSimpleProperty(bean, key, value);
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }
}
