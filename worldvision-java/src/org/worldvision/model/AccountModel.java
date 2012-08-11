package org.worldvision.model;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.worldvision.pojo.Accounts;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class AccountModel {

	public Accounts getAccount(PersistenceManager pm, String letterId) {
		if (letterId != null && !"".equals(letterId)) {
			long id = 0;
			id = Long.parseLong(letterId);
			Key key = KeyFactory.createKey("Accounts", id);
			Accounts account = pm.getObjectById(Accounts.class, key);
			return account;
		} else
			return null;
	}

	public Accounts getAccountByName(PersistenceManager pm,String name) {
		if (name != null && !"".equals(name)) {
			Query query = pm.newQuery(Accounts.class, "account == '" + name
					+ "'");
			Accounts account = null;
			List result = (List<Accounts>) query.execute();
			if (result.size() > 0)
				account = (Accounts) result.get(0);
			return account;
		} else
			return null;
	}

	public List<Accounts> getAvailableVolunteers(PersistenceManager pm) {
		Query query = pm.newQuery(Accounts.class, "role == 'voulenteer'");
		List result = (List<Accounts>) query.execute();
		
		return result;
	}
}
