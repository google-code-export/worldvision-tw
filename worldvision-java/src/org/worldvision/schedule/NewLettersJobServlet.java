package org.worldvision.schedule;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.FileDownload;
import org.worldvision.mail.MailSender;
import org.worldvision.model.AccountModel;
import org.worldvision.model.LetterModel;
import org.worldvision.pojo.Accounts;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;

public class NewLettersJobServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(NewLettersJobServlet.class
			.getName());
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		log.info("called");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Accounts> result = account_model.getAvailableVolunteers(pm);
		int size = result.size();
		log.info("available voulenteers:" + size);
		for (int i = 0; i < size; i++){
			Accounts vou = result.get(i);
			String email = vou.getAccount();
			if (email != null && !"".equals(email) && vou.isWeekly_email()){
				String url = "http://www.worldvision-tw.appspot.com/queue_email?mailId=7&email=" + email + "&id=0";
//				if (email.equals("robbiecheng@gmail.com")){
					log.info("going to send email to " + email);
					System.out.println("going to send email to " + email);
//					MailSender.sendEmail(url);
//				}
			}
		}
	}

}
