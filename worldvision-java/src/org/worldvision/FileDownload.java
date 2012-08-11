	package org.worldvision;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.worldvision.pojo.Countries;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;
import org.worldvision.util.URLUtil;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class FileDownload extends HttpServlet {
	private static final Logger log = Logger.getLogger(FileDownload.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		if (req.getServletPath().indexOf("file_download") > 0)
			doDownload(req, res);
		else if (req.getServletPath().indexOf("country_download") > 0)
			doCountryDownload(req, res);
	}

	private void doCountryDownload(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		long id = 0;
		if (req.getParameter("id") != null)
			id = Long.parseLong(req.getParameter("id"));
		Key key = KeyFactory.createKey("Countries", id);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Countries country = pm.getObjectById(Countries.class, key);
		Blob file = null;
		String type = req.getParameter("type");
		System.out.println("type: " + type);
		String filename = "";
		if ("note".equals(type)){
			filename = country.getNote_url();
			file = country.getNote();
		}
		else if ("template".equals(type)){
			filename = country.getTemplate_url();
			file = country.getTemplate();
		}
		else if ("noun".equals(type)){
			filename = country.getNoun_url();
			file = country.getNoun();
		}

		download(res, file, filename);
	}

	private void doDownload(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		long id = 0;
		if (req.getParameter("id") != null)
			id = Long.parseLong(req.getParameter("id"));
		Key key = KeyFactory.createKey("Letters", id);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Letters letter = pm.getObjectById(Letters.class, key);
		Blob file = letter.getUpload_file();
		String filename = letter.getUpload_file_url();

		download(res, file, filename);
	}

	private void download(HttpServletResponse res, Blob file, String filename)
			throws IOException {
		int length = 0;
		ServletOutputStream op = res.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(filename);

		//
		// Set the response and go!
		//
		//
		res.setContentType((mimetype != null) ? mimetype
				: "application/octet-stream");
		res.setContentLength(file.getBytes().length);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ URLUtil.espaceSpace(filename) + "\"");

		//
		// Stream to the requester.
		//
		byte[] bbuf = new byte[file.getBytes().length];
		ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes());

		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			op.write(bbuf, 0, length);
		}

		in.close();
		op.flush();
		op.close();
	}
}
