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
 * class VoulenteerLog
  include DataMapper::Resource
  
  property :id, Serial
  property :voulenteer_id, String
  property :voulenteer_name, String
  property :return_date, Date
  property :excuse, String
  property :claim_date, Date
  property :letter_id, String 
end

 * 
 */
@PersistenceCapable
public class VoulenteerLogs {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String voulenteer_id;
	@Persistent
	private String voulenteer_name;
	@Persistent
	private Date return_date;
	@Persistent
	private String excuse;
	@Persistent
	private Date claim_date;
	@Persistent
	private String letter_id;
	
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public String getVoulenteer_id() {
		return voulenteer_id;
	}
	public void setVoulenteer_id(String voulenteer_id) {
		this.voulenteer_id = voulenteer_id;
	}
	public String getVoulenteer_name() {
		return voulenteer_name;
	}
	public void setVoulenteer_name(String voulenteer_name) {
		this.voulenteer_name = voulenteer_name;
	}
	public Date getReturn_date() {
		return return_date;
	}
	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}
	public String getExcuse() {
		return excuse;
	}
	public void setExcuse(String excuse) {
		this.excuse = excuse;
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
	
	

}
