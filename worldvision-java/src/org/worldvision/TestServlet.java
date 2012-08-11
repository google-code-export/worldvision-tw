package org.worldvision;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.model.LetterModel;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;

public class TestServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse res){
		LetterModel model = new LetterModel();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List chi_letters = model.findOldLetters("chi", 2);
		List eng_letters = model.findOldLetters("eng", 6);
		
//		Letters l1 = (Letters) chi_letters.get(0);
//		Letters l2 = (Letters) eng_letters.get(0);
		
		pm.close();
		
//		int chi_letters = model.findUnClaimedLetters("chi").size();
//		int eng_letters = model.findUnClaimedLetters("eng").size();
//		int letters = model.findDueLetters(2, true).size();
		
		try {
			PrintWriter writer = res.getWriter();
			writer.println("chi:" + chi_letters);
			writer.println("eng:" + eng_letters);
//			writer.println("due:" + letters);
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
