/**
 * 
 */
package org.worldvision.queue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.mail.MailTemplate;
import org.worldvision.model.LetterModel;
import org.worldvision.model.VoulenteerLogModel;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;
import org.worldvision.pojo.Templates;
import org.worldvision.pojo.VoulenteerLogs;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author robbie
 * 
 */
public class EmailWorker extends HttpServlet {
	private static final Logger logger = Logger.getLogger(EmailWorker.class.getName());
	private LetterModel letterModel = new LetterModel();
	private VoulenteerLogModel logModel = new VoulenteerLogModel();
	public static final String LETTER_VARIABLE = "@letter";
	public static final String DOWNLOAD_URL = "@download_url";
	public static final String UN_CLAIMED_LETTER_COUNT = "@number_of_unclaimed_letters";
	public static final String AVAILABLE_ENG_LETTERS = "@available_eng_letters";
	public static final String AVAILABLE_CHI_LETTERS = "@available_chi_letters";
	public static final String VOLUNTEER_NAME = "@volunteer_name";
	public static final String VOLUNTEER_EMAIL = "@volunteer_email";
	public static final String RETURN_EXECUSE ="@return_execuse";

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("email-worker-called");
		System.out.println(req.getParameter("email"));
		System.out.println("mailId:" + req.getParameter("mailId"));
		String email = req.getParameter("email");
		String letterId = req.getParameter("id");
		String mailId = req.getParameter("mailId");
		String volunteerId = req.getParameter("volunteerId");
		if (email != null && !"".equals(email))
			sendEmail(email, letterId, mailId, volunteerId);
	}

	private void sendEmail(String receipt, String letterId, String mailId, String volunteerId) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			int mail_id = Integer.parseInt(mailId);
			if ( mail_id <=9){
				Letters letter = null;
				String file_name = "";
				
				if (letterId != null && !letterId.equals("0")){
					if (letterId.contains("Letters"))
						letterId = letterId.replace("Letters(", "").replace(")", "");
					letter = letterModel.getLetter(pm, letterId);
					file_name = letter.getUpload_file_name();
					if (file_name != null){
						int index = file_name.lastIndexOf("\\");
						if (index > 0)
							file_name = file_name.substring(index+1, file_name.length());
					}
				}
				
				MimeMessage msg = createEmailMessage(receipt, session);
				switch (mail_id){
					case 1:
						SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
						String dueDate = formatter.format(letter.getDue_date());
						sendDueDateEmail(dueDate, msg, file_name);
						Transport.send(msg);
						break;
					case 2:
						sendThankYouEmail(msg, file_name);
						Transport.send(msg);
						break;
					case 3:
						String blob_key = letter.getReturn_file_url();
						boolean re_upload = letter.isRe_upload();
						sendEmailReturndEmail(msg, blob_key, file_name, re_upload);
						Transport.send(msg);
						break;
					case 4:
						this.sendDueReminderEmail(msg, file_name);
						Transport.send(msg);
						
						String emp_email = letter.getEmployee_id();
						msg = createEmailMessage(emp_email, session);
						this.sendEmpDuedEmergentRemiderEmail(msg, letter, file_name);
						Transport.send(msg);
						break;
					case 5:
						this.sendEmpOldEmergentRemiderEmail(msg, letterId, file_name);
						Transport.send(msg);
						break;
					case 6:
						this.sendEmpCaimLetterNoticeEmail(msg, letterId, file_name);
						Transport.send(msg);
						break;
					case 7:
						int available_eng_letters = letterModel.findUnClaimedLetters("eng").size();
						int available_chi_letters = letterModel.findUnClaimedLetters("chi").size();
						this.sendNewLetterReminderEmail(msg, available_eng_letters, available_chi_letters);
						Transport.send(msg);
						break;
					case 8:
						VoulenteerLogs log = logModel.findLogByVolunteerIdAndLetterId(volunteerId, letterId);
						
						if (log == null)
							logger.info("[ERROR]can't find the log whose volunteerId is " + volunteerId + " and letterId is " + letterId);
						else{
							this.sendEmpReturnedEmergentRemiderEmail(msg, file_name, log);
							Transport.send(msg);
						}
						break;					
				}
				
			}

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e){
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		finally{
			pm.close();
		}
	}

	private MimeMessage createEmailMessage(String receipt, Session session)
			throws MessagingException, UnsupportedEncodingException {
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("nextwvt@worldvision.org.tw",
				"WorldVision Admin"));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
				receipt, ""));
		return msg;
	}

	private void sendEmpOldEmergentRemiderEmail(MimeMessage msg, String fileId, String file_name) {
		String filePath = "http://www.worldvision-tw.appspot.com/file_download?id=" + fileId;
		try {
			msg.setSubject(MailTemplate.EMP_EMERGENT_NOTICE_OLD_TOPIC, "big5");
			msg.setText(replaceVariable(MailTemplate.EMP_EMERGENT_NOTICE_OLD_CONTENT, file_name));
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendEmpReturnedEmergentRemiderEmail(MimeMessage msg, String fileName, VoulenteerLogs log) {
		try {
			String content = MailTemplate.EMP_EMERGENT_NOTICE_RETURNED_CONTENT;
			content = content.replace(this.LETTER_VARIABLE, fileName);
			content = content.replace(this.VOLUNTEER_EMAIL, log.getVoulenteer_id());
			content = content.replace(this.VOLUNTEER_NAME, log.getVoulenteer_name());
			content = content.replace(this.RETURN_EXECUSE, log.getExcuse());
					
			msg.setSubject(MailTemplate.EMP_EMERGENT_NOTICE_RETURNED_TOPIC, "big5");
			msg.setText(content);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendEmpDuedEmergentRemiderEmail(MimeMessage msg, Letters letter, String file_name) {
		try {
			String content = MailTemplate.EMP_EMERGENT_NOTICE_DUED_CONTENT;
			content = content.replace(this.LETTER_VARIABLE, file_name);
			content = content.replace(this.VOLUNTEER_EMAIL, letter.getVoulenteer_account());
			content = content.replace(this.VOLUNTEER_NAME, letter.getVoulenteer_name());
			msg.setSubject(MailTemplate.EMP_EMERGENT_NOTICE_DUED_TOPIC, "big5");
			
			msg.setText(content);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendEmpCaimLetterNoticeEmail(MimeMessage msg, String fileId, String file_name) throws MessagingException {
		String filePath = "http://www.worldvision-tw.appspot.com/file_download?id=" + fileId;
		msg.setSubject(MailTemplate.EMP_CLAIM_LETTER_NOTICE_TOPIC, "big5");
		msg.setText(replaceVariable(MailTemplate.EMP_CLAIM_LETTER_NOTICE_CONTENT, file_name));
	}

	private void sendEmailReturndEmail(MimeMessage msg, String blob_key, String file_name, boolean re_upload) throws MessagingException {
		String filePath = "http://www.worldvision-tw.appspot.com//serve?blob-key=" + blob_key;
		if (re_upload)
			msg.setSubject(MailTemplate.EMPLOYEE_RE_UPLOAD_LETTER_TOPIC, "big5");
		else
			msg.setSubject(MailTemplate.EMPLOYEE_COMPLETE_LETTER_TOPIC, "big5");
		msg.setText(replaceVariable(MailTemplate.EMPLOYEE_COMPLETE_LETTER_CONTENT, file_name).replace(DOWNLOAD_URL, filePath));
	}

	private void sendThankYouEmail(MimeMessage msg, String file_name) throws MessagingException {
		msg.setSubject(MailTemplate.VOLUNETTER_COMPLETE_LETTER_TOPIC, "big5");
		msg.setText(replaceVariable(MailTemplate.VOLUNETTER_COMPLETE_LETTER_CONTENT, file_name));
	}

	private void sendDueDateEmail(String dueDate, MimeMessage msg, String file_name)
			throws MessagingException {
		msg.setSubject(MailTemplate.CLAIM_LETTER_TOPIC, "big5");
		msg.setText(replaceVariable(MailTemplate.CLAIM_LETTER_CONTENT, file_name));
	}
	
	private void sendDueReminderEmail(MimeMessage msg, String file_name)
		throws MessagingException {
		msg.setSubject(MailTemplate.DUE_REMINDER_TOPIC, "big5");
		msg.setText(replaceVariable(MailTemplate.DUE_REMINDER_CONTENT, file_name));
	}
	
	private void sendNewLetterReminderEmail(MimeMessage msg, Integer available_eng_letters, Integer available_chi_letters)
		throws MessagingException {
		msg.setSubject(MailTemplate.NEW_LETTER_REMINDER_TOPIC, "big5");
		msg.setText(MailTemplate.NEW_LETTER_REMINDER_CONTENT
				.replace(AVAILABLE_CHI_LETTERS, available_chi_letters.toString())
				.replace(AVAILABLE_ENG_LETTERS, available_eng_letters.toString()));
	}
	
	private String replaceVariable(String string, String val){
		return string.replace(LETTER_VARIABLE, val);
	}
	
}
