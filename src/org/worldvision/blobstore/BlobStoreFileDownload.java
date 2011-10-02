package org.worldvision.blobstore;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		res.setHeader("Content-Disposition", "attachment; filename=" + info.getFilename());
		blobstoreService.serve(blobKey, res);
	}

}
