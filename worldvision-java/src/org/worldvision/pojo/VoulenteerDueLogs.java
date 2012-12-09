/**
 * 
 */
package org.worldvision.pojo;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * @author robbie
 * 
 * 
 */
@PersistenceCapable
public class VoulenteerDueLogs {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String voulenteer_account;
	@Persistent
	private String voulenteer_name;
	@Persistent
	private Date claim_date;
	@Persistent
	private Date due_date;
	@Persistent
	private String letter_id;
	@Persistent
	private String employee_account;
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public String getVoulenteer_account() {
		return voulenteer_account;
	}
	public void setVoulenteer_account(String voulenteer_id) {
		this.voulenteer_account = voulenteer_id;
	}
	public String getVoulenteer_name() {
		return voulenteer_name;
	}
	public void setVoulenteer_name(String voulenteer_name) {
		this.voulenteer_name = voulenteer_name;
	}
	public Date getClaim_date() {
		return claim_date;
	}
	public void setClaim_date(Date claim_date) {
		this.claim_date = claim_date;
	}
	public String getLetter_id() {
		return letter_id;
	}
	public void setLetter_id(String letter_id) {
		this.letter_id = letter_id;
	}
	public String getEmployee_account() {
		return employee_account;
	}
	public void setEmployee_account(String employee_id) {
		this.employee_account = employee_id;
	}
	public Date getDue_date() {
		return due_date;
	}
	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}
	
}
