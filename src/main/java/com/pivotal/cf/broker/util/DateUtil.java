package com.pivotal.cf.broker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DateUtil {
	public static Date parseDate(String strDate) {
		Date result;
		try {
			result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			result = null;
		}
		
		return result;
	}
	
	public static Date afterHours(Date date, Long hours) {
		Long time = date.getTime();
		time += hours * 60 * 60 *1000;
		return new Date(time);
	}
	
	public static Date beforeHours(Date date, Long hours) {
		Long time = date.getTime();
		time -= hours * 60 * 60 *1000;
		return new Date(time);
	}
	
	public static Date beforeMinutes(Date date, Long minutes) {
		Long time = date.getTime();
		time -= minutes * 60 *1000;
		return new Date(time);
	}
	
	public static Date endOfDay(Date date) {
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date) + " 23:59:59";
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
    
    public static Date endOfDay(Date date, String timezone) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date) + " 23:59:59";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }
	
	public static Date startOfDay(Date date) {
		String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date) + " 00:00:00";
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
    public static Date startOfDay(Date date, String timezone) {
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date) + " 00:00:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            return null;
        }
    }
	   
	public static Date endOfHour(Date date) {
		String strDate = new SimpleDateFormat("yyyy-MM-dd HH:").format(date) + "59:59";
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date startOfHour(Date date) {
		String strDate = new SimpleDateFormat("yyyy-MM-dd HH:").format(date) + "00:00";
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Map<String,Integer> getTime(Double time)throws Exception{
	    Map<String,Integer> map=new HashMap<String, Integer>();
	    try {
	        double dday=time/(3600*24);
	        int day=(int) (time/(3600*24));
	        double dhour=dday-day;
	        int hour=(int) (dhour*24);
	        dhour=dhour*24;
	        double dminute=dhour-hour;
	        int minute=(int) (dminute*60);
	        dminute=dminute*60;
	        double dsecond=dminute-minute;
	        int second=(int) (dsecond*60);
	        
	        map.put("day", day);
	        map.put("hour", hour);
	        map.put("minute", minute);
	        map.put("second", second);
        } catch (Exception e) {
            throw new Exception(e);
        }
	    return map;
	}

	public static String format(Date date) {
		if(date == null) {
			return null;
		}
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	

    public static String format(Date date, String format) {
        if(date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

}
