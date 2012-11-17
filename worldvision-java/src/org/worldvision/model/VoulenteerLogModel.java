package org.worldvision.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.worldvision.pojo.Accounts;
import org.worldvision.pojo.Letters;
import org.worldvision.pojo.PMF;
import org.worldvision.pojo.VoulenteerLogs;


public class VoulenteerLogModel {

	public List findLogs(Date start_date, Date end_date) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<Letters> result = new ArrayList();

		// Query query = pm.newQuery("select * from " +
		// VoulenteerLogs.class.getName());
		Query query = pm.newQuery(VoulenteerLogs.class);
		if (start_date != null && end_date != null) {
			query.setFilter("return_date >= s_date && return_date <= e_date");
			query.declareImports("import java.util.Date");
			query.declareParameters("Date s_date, Date e_date");
			result = (List<Letters>) query.execute(start_date, end_date);
		}
		else
			result =  (List<Letters>) query.execute();

		// pm.close();
		return result;

	}
	
	public VoulenteerLogs findLogByVolunteerIdAndLetterId(String volunteerId, String letterId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// Query query = pm.newQuery("select * from " +
		// VoulenteerLogs.class.getName());
		//Query query = pm.newQuery(Accounts.class, "role == 'voulenteer'");
		Query query = pm.newQuery(VoulenteerLogs.class);
		List<VoulenteerLogs> result = new ArrayList();
		try {
			query.setFilter("voulenteer_id == '" + volunteerId + "' && letter_id == '" + letterId + "'");
			result = (List<VoulenteerLogs>) query.execute();
			if (result != null && result.size() >0)
				return result.get(0);
			else 
				return null;
		} finally {
			query.closeAll();
		}
	}
	

}
