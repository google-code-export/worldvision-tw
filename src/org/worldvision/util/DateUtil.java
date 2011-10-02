package org.worldvision.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static void main(String[] args){
		System.out.println(getCurrentDate());
	}
	
	public static Date getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 8);
		
		return cal.getTime();
	}

}
