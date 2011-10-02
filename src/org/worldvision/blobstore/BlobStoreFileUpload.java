package org.worldvision.blobstore;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobStoreFileUpload extends HttpServlet {
	

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private BlobInfoFactory blobinfoFactory = new BlobInfoFactory();

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		
		BlobKey blobKey = blobs.get("myFile");
		String nextUrl = req.getParameter("to");
		String fromUrl = req.getParameter("from");
		System.out.println("from: " + nextUrl);
		System.out.println("to: " + fromUrl);
		
		if (blobKey == null) {
			System.out.println("upload failed");
		    
			res.sendRedirect(fromUrl);
		} else {
			System.out.println("upload success");
			BlobInfo info = blobinfoFactory.loadBlobInfo(blobKey);
			String fileName = info.getFilename();
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextUrl);
			req.setAttribute("blobKey", blobKey.getKeyString());
			req.setAttribute("fileName", fileName);
			
			dispatcher.forward(req,res); 
		}
	}

}
