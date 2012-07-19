package org.worldvision;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.worldvision.model.AccountModel;
import org.worldvision.model.Accounts;
import org.worldvision.model.Countries;
import org.worldvision.model.Letters;
import org.worldvision.model.PMF;
import org.worldvision.model.Templates;
import org.worldvision.util.DateUtil;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class FileUpload extends HttpServlet {
	private static final Logger log = Logger.getLogger(FileUpload.class
			.getName());
	private AccountModel account_model = new AccountModel();

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println("java:debug:upload");
		if (req.getServletPath().indexOf("return_file") > 0) {
			// System.out.println("java:debug:return_file");
			doReturnFileUpload(req, res);
		} else if (req.getServletPath().indexOf("file_upload") > 0) {
			// System.out.println("java:debug:file_upload");
			doFileUpload(req, res);
		} else if (req.getServletPath().indexOf("country_upload") > 0) {
			doCountryUpload(req, res);
		} else if (req.getServletPath().indexOf("template_upload") > 0){
			doTemplateUpload(req, res);
		}
	}

	private void doTemplateUpload(HttpServletRequest req,
			HttpServletResponse res) {
		System.out.println("java:debug:upload:template:1");
		try {
			String blobKey = (String) req.getAttribute("blobKey");
			if (blobKey != null) {
				long id = Long.parseLong(req.getParameter("id"));
				if (id == 0){
					System.out.println("java:debug:upload:template:2");
					Templates template = new Templates();
					template.setTempate_url(blobKey);
					PersistenceManager pm = PMF.get().getPersistenceManager();
					pm.makePersistent(template);
				} else{
					System.out.println("java:debug:upload:template:3");
					Key key = KeyFactory.createKey("Templates", id);
					PersistenceManager pm = PMF.get().getPersistenceManager();
					Templates template = pm.getObjectById(Templates.class, key);
					template.setTempate_url(blobKey);
					try {
						pm.makePersistent(template);
					} finally {
						pm.close();
					}
				}
			}
			res.sendRedirect("/employee/country");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void doCountryUpload(HttpServletRequest req, HttpServletResponse res) {
//		Object[] data;
		try {
//			data = doUpload(req, res);
			String blobKey = (String) req.getAttribute("blobKey");
			if (blobKey != null) {
//				Blob content = (Blob) data[0];
//				String fileName = (String) data[1];
				String fileUrl = blobKey;
				String fileName = (String) req.getAttribute("fileName");
				long id = 0;
				id = Long.parseLong(req.getParameter("id"));
				Key key = KeyFactory.createKey("Countries", id);
				PersistenceManager pm = PMF.get().getPersistenceManager();
				Countries country = pm.getObjectById(Countries.class, key);
				String type = req.getParameter("type");
				System.out.println("type: " + type);
				if ( "note".equals(type)) {
					System.out.println("save note");
//					country.setNote(content);
					country.setNote_file_name(fileName);
					country.setNote_url(fileUrl);
				}
				else if ("template".equals(type)){
					System.out.println("save template");
//					country.setTemplate(content);
					country.setTemplate_file_name(fileName);
					country.setTemplate_url(fileUrl);
				}
				else if ("noun".equals(type)){
					System.out.println("save noun");
//					country.setNoun(content);
					country.setNoun_file_name(fileName);
					country.setNoun_url(fileUrl);
				}
				else if ("background".equals(type)){
					System.out.println("save background");
//					country.setNoun(content);
					
					country.setBackground_file_name(fileName);
					country.setBackground_url(fileUrl);
				}
				
				try {
					pm.makePersistent(country);
				} finally {
					pm.close();
				}

			}

			res.sendRedirect("/employee/country");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doFileUpload(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
//		Object[] data = doUpload(req, res);
		String userName = req.getParameter("id");
		System.out.println("id=>" + userName);
		String type = req.getParameter("type");
		System.out.println("type=>" + type);
		type = (type == null) ? "eng" : type;
		String blobKey = (String) req.getAttribute("blobKey");
		
//		String blobKey = (String) req.getParameterMap().get("blob-);
		if (blobKey != null ) {
//			Blob content = (Blob) data[0];
			String fileName = (String) req.getAttribute("fileName");
			String fileUrl = blobKey;
			Letters letter = new Letters(userName, fileUrl, fileName, type);
			if (type != null && "chi".equals(type))
				letter.setNote("中翻英");
			letter.setShow("false");
			PersistenceManager pm = PMF.get().getPersistenceManager();
			System.out.println("filename: " + fileName);
			try {
				pm.makePersistent(letter);
			} finally {
				pm.close();
			}

		}
		res.sendRedirect("/employee");
	}

	private void doReturnFileUpload(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
//		Object[] data = doUpload(req, res);
		String blobKey = (String) req.getAttribute("blobKey");
		
		Letters letter = null;
		Accounts vou = null;
		Accounts emp = null;
		long id = 0;
		if (blobKey != null) {
			if (req.getParameter("id") != null && req.getAttribute("fileName") != null && !"".equals(req.getAttribute("fileName"))) {
				String fileName = (String) req.getAttribute("fileName");
				String fileUrl = blobKey;
					
				id = Long.parseLong(req.getParameter("id"));
				Key key = KeyFactory.createKey("Letters", id);
				PersistenceManager pm = PMF.get().getPersistenceManager();
				PersistenceManager pm2 = PMF.get().getPersistenceManager();
				letter = pm.getObjectById(Letters.class, key);
				letter.setReturn_file_url(fileUrl);
//				letter.setReturn_file((Blob) data[0]);
//				letter.setReturn_file_url((String) data[1]);
				letter.setRe_upload(false);
				letter.setReturn_file_url(fileUrl);
				letter.setReturn_file_name(fileName);
				letter.setStatus("returned");
				letter.setRerturn_date(DateUtil.getCurrentDate());
				
				vou = account_model.getAccountByName(pm2, letter.getVoulenteer_account());
				String emp_id = letter.getEmployee_id();
				System.out.println("pm: " + pm.toString());
				emp = account_model.getAccountByName(pm, emp_id);
				int jobs = vou.getJobs();
				jobs -= 1;
				vou.setJobs(jobs);
					
				try {
					pm.makePersistent(letter);
					pm2.makePersistent(vou);
					
				} finally {
					pm.close();
					pm2.close();
				}
				
				if (vou != null){
					URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
					if (vou.getAccount() != null){
						String email = vou.getAccount();
						URL url = new URL("http://www.worldvision-tw.appspot.com/queue_email?mailId=2&email=" + email + "&id=" + Long.toString(id));
						fetcher.fetchAsync(url);
					}
					
				}
				if (emp != null){
					URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
					if (emp.getAccount() != null){
						String email = emp.getAccount();
						URL url = new URL("http://www.worldvision-tw.appspot.com/queue_email?mailId=3&email=" + email + "&id=" + Long.toString(id));
						fetcher.fetchAsync(url);
					}
				}
			}
		}

		res.sendRedirect("/volunteer");
	}

	private Object[] doUpload(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Object[] return_data = null;
		try {
			ServletFileUpload upload = new ServletFileUpload();
			upload.setHeaderEncoding("utf-8");
			res.setContentType("text/plain");

			FileItemIterator iterator = upload.getItemIterator(req);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					log.warning("Got a form field: " + item.getFieldName());
				} else {
					log.warning("Got an uploaded file: " + item.getFieldName()
							+ ", name = " + item.getName());

					// save into datastore
					String name = item.getName();
					if (name != null && !"".equals(name) && stream != null) {
						Blob content = new Blob(IOUtils.toByteArray(stream));
						return_data = new Object[2];
						return_data[0] = content;
						return_data[1] = item.getName();
					}

					// You now have the filename (item.getName() and the
					// contents (which you can read from stream). Here we just
					// print them back out to the servlet output stream, but you
					// will probably want to do something more interesting (for
					// example, wrap them in a Blob and commit them to the
					// datastore).
					// int len;
					// byte[] buffer = new byte[8192];
					// while ((len = stream.read(buffer, 0, buffer.length)) !=
					// -1) {
					// res.getOutputStream().write(buffer, 0, len);
					// }
				}
			}
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
		return return_data;
	}

}
