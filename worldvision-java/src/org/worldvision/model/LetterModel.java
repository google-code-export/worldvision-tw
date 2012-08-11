package org.worldvision.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;
import org.worldvision.util.DateUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class LetterModel {
	
	public static final String TRANS_TYPE_CHINESE_TO_ENGLISH = "chi";
	public static final String TRANS_TYPE_ENGLISH_TO_CHINESE = "eng";
	
	//YYYY-MM-DD HH:MM:SS
	String pattern = "yyyy/MM/dd MM/dd/yyyy ";
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    
    
    public List findUnClaimedLetters(String type){
    	PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = new ArrayList();
		
		Query query = pm.newQuery(Letters.class);
		try {
			query.setFilter("claim_date == null && trans_type =='" + type + "'");
			result = (List<Letters>) query.execute();
			return result;
		} finally {
			query.closeAll();
		}

    }
	
	public List findDueLetters(int days, boolean send) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = new ArrayList();

		Calendar cal = DateUtil.getCurrentCalendarOfTPE();
		// if (days > 0)
		cal.add(Calendar.DATE, -days);
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.SECOND, 0);
		Date date = cal.getTime();

		Query query = pm.newQuery(Letters.class);
		if (send = false)
			query.setFilter("due_date != null && due_date < date && return_file_url == null && send_due_reminder == false");
		else
			query.setFilter("due_date != null && due_date < date && return_file_url == null");
		query.declareImports("import java.util.Date");
		query.declareParameters("Date date");
		result = (List<Letters>) query.execute(date);

		int size = result.size();
		for (int i = 0; i < size; i++) {
			Letters letter = result.get(i);
			System.out.println(letter.getId());
			System.out.println(letter.getClaim_date());
			System.out.println(letter.getDue_date());
		}
		pm.close();
		return result;

	}

	public List findReturnedLetters(Date begin, Date end) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = new ArrayList();

		Calendar cal = DateUtil.getCurrentCalendarOfTPE();
		//
		Date date = cal.getTime();
		System.out.println("date: " + date.toGMTString());

		Query query = pm.newQuery(Letters.class);

		if (begin != null && end != null) {
			query.setFilter("return_date != null && return_date >= begin && return_date <= end");
			query.declareImports("import java.util.Date");
			query.declareParameters("Date begin, Date end");
			result = (List<Letters>) query.execute(begin, end);
		} else {
			query.setFilter("return_date != null");
			result = (List<Letters>) query.execute(begin, end);
		}

		int size = result.size();
		for (int i = 0; i < size; i++) {
			Letters letter = result.get(i);
			System.out.println(letter.getId());
			System.out.println(letter.getClaim_date());
			System.out.println(letter.getDue_date());
		}
		pm.close();
		return result;

	}

	public List findOldLetters(String type, int days) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = new ArrayList();

		Calendar cal = DateUtil.getCurrentCalendarOfTPE();
		// if (days > 0)
		cal.add(Calendar.DATE, -days);
		Date date = cal.getTime();
		System.out.println("date: " + date.toGMTString());
		Query query = pm.newQuery(Letters.class);
		try {
			query.setFilter("create_date < date && trans_type=='" + type +"' && deleted == false && status == 'unclaimed'");
			query.declareImports("import java.util.Date");
			query.declareParameters("Date date");
			System.out.println("query:" + query.toString());
			result = (List<Letters>) query.execute(date);

			int size = result.size();
			for (int i = 0; i < size; i++) {
				Letters letter = result.get(i);
				System.out.println(letter.getId());
				System.out.println(letter.getClaim_date());
				System.out.println(letter.getDue_date());
			}
			return result;
		} finally {
			pm.close();
		}

	}

	public List find3DaysDueLetters(PersistenceManager pm, int days) {
		List<Letters> result = new ArrayList();

		Calendar cal = DateUtil.getCurrentCalendarOfTPE();
		// if (days > 0)
		cal.add(Calendar.DATE, -days);
		Date date = cal.getTime();
		System.out.println("date: " + date.toGMTString());

		Query query = pm.newQuery(Letters.class);
		try {
			query.setFilter("due_date_3 < date && return_file_url == null && (status == '已領取')");
			query.declareImports("import java.util.Date");
			query.declareParameters("Date date");
			result = (List<Letters>) query.execute(date);

			int size = result.size();
			for (int i = 0; i < size; i++) {
				Letters letter = result.get(i);
				System.out.println(letter.getId());
				System.out.println(letter.getClaim_date());
				System.out.println(letter.getDue_date());
			}
			return result;
		} finally {
			query.closeAll();
		}

	}

	public Letters getLetter(PersistenceManager pm, String letterId) {
		if (letterId != null) {
			long id = 0;
			id = Long.parseLong(letterId);
			Key key = KeyFactory.createKey("Letters", id);
			Letters letter = pm.getObjectById(Letters.class, key);
			return letter;
		} else
			return null;
	}
}
