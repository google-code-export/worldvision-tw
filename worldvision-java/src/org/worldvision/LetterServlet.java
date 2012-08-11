package org.worldvision;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

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
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
@Deprecated
public class LetterServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(LetterServlet.class.getName());
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String id = "";
		String acc_id = "";
		PersistenceManager pm = PMF.get().getPersistenceManager();
		if (req.getParameter("id") != null && req.getParameter("account_id") != null){
			acc_id = req.getParameter("account_id");
			id = req.getParameter("id");
			
			Accounts acc = account_model.getAccount(pm, acc_id);
			int jobs = acc.getJobs();
			jobs += 1;
			acc.setJobs(jobs);
			
			Letters letter = letter_model.getLetter(pm, id);
			letter.setVoulenteer_id(acc.getVoulenteer_id());
			letter.setVoulenteer_name(acc.getAccount());
			letter.setClaim_date(new Date());
			Calendar cal = Calendar.getInstance();
			//todo change to 7
			cal.add(Calendar.DATE, +1);
			Calendar cal2 = Calendar.getInstance();
			//todo change to 10
			cal2.add(Calendar.DATE, +2);
			letter.setDue_date(cal.getTime());
			letter.setDue_date_3(cal2.getTime());
			letter.setStatus("已領取");
			System.out.println("國家: " + letter.getCountry());
			
			
			try {
				pm.makePersistent(letter);
			} finally {
				pm.close();
			}
			
//			"http://www.worldvision-tw.appspot.com/queue_email?email=" + current_user[:email] + "&id=" + id.to_s
			if (acc.getAccount() != null){
				String email = acc.getAccount();
				String url = "http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + email + "&id=" + id;
				MailSender.sendEmail(url);
			}
		}
		res.sendRedirect("/volunteer");
	}
}
