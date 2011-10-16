/**
 * 
 */
package org.worldvision.queue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;
import org.worldvision.LetterServlet;
import org.worldvision.mail.MailTemplate;
import org.worldvision.model.LetterModel;
import org.worldvision.model.Letters;
import org.worldvision.model.PMF;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author robbie
 * 
 */
public class EmailWorker extends HttpServlet {
	private static final Logger log = Logger.getLogger(EmailWorker.class.getName());
	private LetterModel model = new LetterModel();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("email-worker-called");
		System.out.println(req.getParameter("email"));
		String email = req.getParameter("email");
		String letterId = req.getParameter("id");
		String mailId = req.getParameter("mailId");
		if (email != null && !"".equals(email))
			sendEmail(email, letterId, mailId);
	}

	private void sendEmail(String receipt, String letterId, String mailId) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			//fetch attachment
			
			int mail_id = Integer.parseInt(mailId);
			if (letterId!= null && !"".equals(letterId) && mail_id <5){
				Letters letter = model.getLetter(pm, letterId);
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				String dueDate = formatter.format(letter.getDue_date());
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress("robbie@fliptop.com",
						"WorldVision Admin"));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						receipt, ""));
				switch (mail_id){
					case 1: 
						sendDueDateEmail(dueDate, msg);
						break;
					case 2:
						sendThankYouEmail(msg);
						break;
					case 3:
						sendEmailReturndEmail(msg, letterId);
						break;
					case 4:
						this.sendDueReminderEmail(msg);
						break;
				}
				System.out.println("ready to send");
				Transport.send(msg);
				System.out.println("send out email");
			}
			else if (mail_id == 5){
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress("robbiecheng@gmail.com",
						"WorldVision Admin"));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						receipt, "robbiecheng"));
				log.info("going to send new letter email to " + receipt);
				this.sendNewLetterReminderEmail(msg);
				Transport.send(msg);
			}
//			Blob file = letter.getUpload_file();
//			String fileName = letter.getUpload_file_url();
//			System.out.println("letter_id: "+letter.getId().toString());
//			System.out.println("file_name: "+fileName);
			// attache the file
//			String htmlBody = "123";
//			byte[] attachmentData = file.getBytes();

//			Multipart mp = new MimeMultipart();
//
//			MimeBodyPart htmlPart = new MimeBodyPart();
//			htmlPart.setContent(htmlBody, "text/html");
//			mp.addBodyPart(htmlPart);
			
			//try xml instead
//			File xml = new File("queue.xml");
//			
//			MimeBodyPart attachment = new MimeBodyPart();
//			attachment.setFileName("queue.xml");
//			attachment.setContent(xml, "text/xml");
//			mp.addBodyPart(attachment);
//
//			msg.setContent(mp);
		} catch (AddressException e) {
			// ...
		} catch (MessagingException e) {
			// ...
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			pm.close();
		}
	}

	private void sendEmailReturndEmail(MimeMessage msg, String fileId) throws MessagingException {
		String filePath = "http://www.worldvision-tw.appspot.com/file_download?id=" + fileId;
		msg.setSubject(MailTemplate.EMPLOYEE_COMPLETE_LETTER_TOPIC, "big5");
		msg.setText(MailTemplate.EMPLOYEE_COMPLETE_LETTER_CONTENT);
	}

	private void sendThankYouEmail(MimeMessage msg) throws MessagingException {
		msg.setSubject(MailTemplate.VOLUNETTER_COMPLETE_LETTER_TOPIC, "big5");
		msg.setText(MailTemplate.VOLUNETTER_COMPLETE_LETTER_CONTENT);
	}

	private void sendDueDateEmail(String dueDate, MimeMessage msg)
			throws MessagingException {
		msg.setSubject(MailTemplate.CLAIM_LETTER_TOPIC, "big5");
		msg.setText(MailTemplate.CLAIM_LETTER_CONTENT);
	}
	
	private void sendDueReminderEmail(MimeMessage msg)
		throws MessagingException {
		msg.setSubject(MailTemplate.DUE_REMINDER_TOPIC, "big5");
		msg.setText(MailTemplate.DUE_REMINDER_CONTENT);
	}
	
	private void sendNewLetterReminderEmail(MimeMessage msg)
		throws MessagingException {
		msg.setSubject(MailTemplate.NEW_LETTER_REMINDER_TOPIC, "big5");
		msg.setText(MailTemplate.NEW_LETTER_REMINDER_CONTENT);
	}
	
}
