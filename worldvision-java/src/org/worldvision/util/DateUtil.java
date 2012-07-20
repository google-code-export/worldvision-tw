package org.worldvision.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	
	public static void main(String[] args){
		System.out.println(getCurrentDate());
	}
	
	public static Date getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 8);
		
		return cal.getTime();
	}
	
	public static Calendar getCurrentCalendarOfTPE(){
		return Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
	}

}
