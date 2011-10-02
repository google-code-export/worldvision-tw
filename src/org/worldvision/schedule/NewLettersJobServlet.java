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
import org.worldvision.model.Accounts;
import org.worldvision.model.LetterModel;
import org.worldvision.model.Letters;
import org.worldvision.model.PMF;

public class NewLettersJobServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(NewLettersJobServlet.class
			.getName());
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Accounts> result = account_model.getAvailableVolunteers(pm);
		int size = result.size();
		log.info("available voulenteers:" + size);
		for (int i = 0; i < size; i++){
			Accounts vou = result.get(i);
			String email = vou.getEmail();
			if (email != null && !"".equals(email)){
				log.info("going to send email to " + email);
				System.out.println("going to send email to " + email);
				String url = "http://www.worldvision-tw.appspot.com/queue_email?mailId=5&email=" + email + "&id=0";
				MailSender.sendEmail(url);
			}
		}
	}

}
