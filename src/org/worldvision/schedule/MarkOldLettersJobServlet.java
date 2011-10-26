package org.worldvision.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.mail.MailSender;
import org.worldvision.model.AccountModel;
import org.worldvision.model.Accounts;
import org.worldvision.model.LetterModel;
import org.worldvision.model.Letters;
import org.worldvision.model.PMF;

public class MarkOldLettersJobServlet extends HttpServlet {
	private LetterModel letter_model = new LetterModel();
	private AccountModel account_model = new AccountModel();
	  
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> letters = new ArrayList();
		letters.addAll(letter_model.findOldLetters(pm, "chinese", 2));
		letters.addAll(letter_model.findOldLetters(pm, "english", 7));
		
		int size = letters.size();
		for (int i = 0; i < size; i++){
			Letters letter = letters.get(i);
			letter.setStatus("緊急");
			pm.makePersistent(letter);
		}
	}

}
