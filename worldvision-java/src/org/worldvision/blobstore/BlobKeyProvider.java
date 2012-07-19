package org.worldvision.blobstore;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class BlobKeyProvider extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		PrintWriter writer = res.getWriter();
		String url = blobstoreService.createUploadUrl("/upload");
		System.out.println(url);
		writer.print(url);
		writer.close();
		
	}

}
