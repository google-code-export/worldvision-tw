package org.worldvision.schedule;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.mail.MailSender;
import org.worldvision.model.AccountModel;
import org.worldvision.model.LetterModel;
import org.worldvision.pojo.Accounts;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class MarkOldLettersAsEmergentJobServlet extends HttpServlet {
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	  
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> letters = new ArrayList();
		letters.addAll(letter_model.findOldLetters(LetterModel.TRANS_TYPE_CHINESE_TO_ENGLISH, 2));
		letters.addAll(letter_model.findOldLetters(LetterModel.TRANS_TYPE_ENGLISH_TO_CHINESE, 7));
		
		int size = letters.size();
		for (int i = 0; i < size; i++){
			Letters letter = letters.get(i);
			if ("unclaimed".equals(letter.getStatus())){
				letter.setStatus("emergent");
				pm.makePersistent(letter);
				sendLetterToEmp(letter.getEmployee_id(), new Long(letter.getId().getId()).toString());
			}
		}
	}
	
	private void sendLetterToEmp(String email, String id){
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		URL url;
		try {
			url = new URL("http://www.worldvision-tw.appspot.com/queue_email?mailId=5&email=" + email + "&id=" + id);
			fetcher.fetchAsync(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
