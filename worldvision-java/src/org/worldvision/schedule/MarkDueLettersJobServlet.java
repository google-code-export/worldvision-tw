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
import org.worldvision.pojo.VoulenteerDueLogs;

public class MarkDueLettersJobServlet extends HttpServlet {
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = letter_model.findDueLetters(2, true);
		int size = result.size();
		System.out.println("found due letter: " + size);
		for (int i = 0; i < size; i++){
			Letters letter = result.get(i);
			
			VoulenteerDueLogs log = new VoulenteerDueLogs();
			log.setClaim_date(letter.getClaim_date());
			log.setEmployee_account(letter.getEmployee_id());
			log.setLetter_id(letter.getId().toString());
			log.setVoulenteer_account(letter.getVoulenteer_account());
			log.setVoulenteer_name(letter.getVoulenteer_name());
			log.setClaim_date(letter.getClaim_date());
			log.setDue_date(letter.getDue_date());
			
			pm.makePersistent(log);
			
			letter.setClaim_date(null);
			letter.setVoulenteer_id(null);
			letter.setVoulenteer_name(null);
			letter.setVoulenteer_account(null);
			letter.setClaim_date(null);
			letter.setDue_date(null);
			letter.setDue_date_3(null);
			letter.setStatus("emergent");
			letter.setShow("true");
			
			pm.makePersistent(letter);
		}
	}

}
