package org.worldvision.migration;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.worldvision.model.LetterModel;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;
import org.worldvision.util.DateUtil;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class MigrationServlet extends HttpServlet{
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private LetterModel letterModel = new LetterModel();
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Calendar cal = DateUtil.getCurrentCalendarOfTPE();
		// if (days > 0)
		cal.add(Calendar.DATE, -30);
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.SECOND, 0);
		Date date = cal.getTime();
		
		List<Letters> letters = letterModel.findByCreatedDate(pm, date);
		try{
			for (Letters letter : letters){
				String old_upload_key = letter.getUpload_file_url();
				if (old_upload_key !=  null){
					String new_upload_key = getMigratedBlobKey(old_upload_key);
					if (new_upload_key != null){
						letter.setUpload_file_url(new_upload_key);
					}
				}
				String old_return_key = letter.getReturn_file_url();
				if (old_return_key != null){
					String new_return_key = getMigratedBlobKey(old_return_key);
					if (new_return_key != null){
						letter.setReturn_file_url(new_return_key);
					}
				}
				
				pm.makePersistent(letter);
			}
		}finally{
			pm.close();
		}
		
	
	}
	
	public String getMigratedBlobKey(String oldKey) {
		  
		  
		  
		  String migrationEntityKey = "__BlobMigration__";
		  Key createKey = KeyFactory.createKey(migrationEntityKey, oldKey);
		  Entity migrationEntity;
		try {
			migrationEntity = datastore.get(createKey);
			BlobKey newKey = (BlobKey) migrationEntity.getProperty("new_blob_key");
			return newKey.getKeyString();
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		return null;  
		  
	}

}
