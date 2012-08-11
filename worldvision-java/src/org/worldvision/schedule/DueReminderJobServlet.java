package org.worldvision.schedule;

import java.io.IOException;
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

public class DueReminderJobServlet extends HttpServlet {
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = letter_model.findDueLetters(0, false);
		System.out.println("found " + result.size() + " letters");
		int size = result.size();
		try {
			for (int i = 0; i < size; i++) {
				Letters letter = result.get(i);
				String vou_name = letter.getVoulenteer_account();
				if (vou_name != null && !letter.isSend_due_reminder()) {
					Accounts vou = account_model.getAccountByName(pm, vou_name);
					if (vou != null) {
						String email = vou.getAccount();
						String letter_id = letter.getId().toString();
						if (email != null && !"".equals(email)) {
							System.out.println("going to send email to "
									+ email);
							String url = "http://www.worldvision-tw.appspot.com/queue_email?mailId=4&email="
									+ email + "&id=" + letter_id;
							MailSender.sendEmail(url);
							letter.setSend_due_reminder(true);
							pm.makePersistent(letter);
						}
					}
				}

			}
		} finally {
			pm.close();
		}

	}

}
