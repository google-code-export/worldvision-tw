package org.worldvision.pojo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * class Account
  include DataMapper::Resource
  
  property :id, Serial
  property :account, String
  property :password, String
  property :role, String
  property :name, String
  property :email, String
  property :voulenteer_id, String
  property :voulenteer_type, String
end

 * @author robbie
 *
 */
@PersistenceCapable
public class Accounts {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String account;
	@Persistent
	private String role;
	@Persistent
	private String name;
	@Persistent
	private String email;
	@Persistent
	private String voulenteer_id;
	@Persistent
	private String voulenteer_type;
	@Persistent
	private int jobs;
	private Boolean weekly_email;
	private Boolean allow_login;
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVoulenteer_id() {
		return voulenteer_id;
	}
	public void setVoulenteer_id(String voulenteer_id) {
		this.voulenteer_id = voulenteer_id;
	}
	public String getVoulenteer_type() {
		return voulenteer_type;
	}
	public void setVoulenteer_type(String voulenteer_type) {
		this.voulenteer_type = voulenteer_type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getJobs() {
		return jobs;
	}

	public void setJobs(int jobs) {
		this.jobs = jobs;
	}
	public Boolean isWeekly_email() {
		return weekly_email;
	}
	public void setWeekly_email(Boolean weekly_email) {
		this.weekly_email = weekly_email;
	}
	
	public Boolean getAllow_login() {
		return allow_login;
	}
	public void setAllow_login(Boolean allow_login) {
		this.allow_login = allow_login;
	}
	public Boolean getWeekly_email() {
		return weekly_email;
	}
	

}
