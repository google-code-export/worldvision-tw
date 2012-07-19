package org.worldvision.blobstore;

import java.io.IOException;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.util.URLUtil;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobStoreFileDownload extends HttpServlet {

	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		BlobInfoFactory blobinfoFactory = new BlobInfoFactory();
		BlobInfo info = blobinfoFactory.loadBlobInfo(blobKey);
		
		res.setContentType(info.getContentType());
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setHeader("charset", "utf-8");
		res.setCharacterEncoding("utf-8");
		String filename = info.getFilename();
		if (filename.indexOf("\\") > 0)
			filename = filename.substring(filename.lastIndexOf("\\") + 1);
		
		String user_agent = req.getHeader("user-agent");	
		boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
		if (isInternetExplorer) {
			filename = URLEncoder.encode(filename, "utf-8");
		} else {
		    filename = MimeUtility.encodeText(filename);
		}
		System.out.println("filename: " + filename);
		filename = URLUtil.espaceSpace(filename);
		res.setHeader("Content-Disposition", "attachment; filename=" + filename);
		blobstoreService.serve(blobKey, res);
	}

}
