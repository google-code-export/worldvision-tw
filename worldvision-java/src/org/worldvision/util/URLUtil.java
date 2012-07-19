package org.worldvision.util;

public class URLUtil {
	
	public static String espaceSpace(String string){
		if (string == null)
			return null;
		else
			return string.replaceAll(" ", "%20").replaceAll("\\+", "%20");
		
	}

}
