package org.worldvision.mail;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class MailSender {
	public static void sendEmail(String url) {
		if (url != null && !"".equals(url)){
			URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
			URL _url;
			try {
				_url = new URL(url);
				fetcher.fetchAsync(_url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
