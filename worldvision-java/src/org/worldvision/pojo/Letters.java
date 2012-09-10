/**
 * 
 */
package org.worldvision.pojo;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.worldvision.util.DateUtil;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * @author robbie
 * 
 */
@PersistenceCapable
public class Letters {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private Date create_date;
	@Persistent
	private String employee_id;
	@Persistent
	private String country_name;
	@Persistent
	private String country_id;
	@Persistent
	private String type;
	@Persistent
	private String note;
	@Persistent
	private String status;
	@Persistent
	private String voulenteer_id;
	@Persistent
	private String voulenteer_name;
	@Persistent
	private String voulenteer_account;
	@Persistent
	private Date claim_date;
	@Persistent
	private Date due_date;
	@Persistent
	private Date due_date_3;
	@Persistent
	private Date return_date;
	@Persistent
	private Blob upload_file;
	@Persistent
	private String upload_file_url;
	@Persistent
	private String upload_file_name;
	@Persistent
	private Blob return_file;
	@Persistent
	private String return_file_url;
	@Persistent
	private String return_file_name;
	@Persistent
	private String trans_type;
	@Persistent
	private int number_of_letters;
	@Persistent
	private boolean send_due_reminder;
	@Persistent
	private String show;
	@Persistent
	private boolean deleted;
	@Persistent
	private boolean re_upload;
	@Persistent
	private String letter_source_type;
	@Persistent
	private Integer return_days;

	public boolean isRe_upload() {
		return re_upload;
	}

	public void setRe_upload(boolean re_upload) {
		this.re_upload = re_upload;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public boolean isSend_due_reminder() {
		return send_due_reminder;
	}

	public void setSend_due_reminder(boolean send_due_reminder) {
		this.send_due_reminder = send_due_reminder;
	}

	public Letters(String employeeId, String upload_file_url, String upload_file_name, String type) {
		super();
		this.create_date = DateUtil.getCurrentDate();
		this.employee_id = employeeId;
		this.status = "unclaimed";
		this.trans_type = type;
		this.upload_file_url = upload_file_url;
		this.upload_file_name = upload_file_name;

	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}

	public String getCountry() {
		return country_name;
	}

	public void setCountry(String country) {
		this.country_name = country;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Date getClaim_date() {
		return claim_date;
	}

	public void setClaim_date(Date claim_date) {
		this.claim_date = claim_date;
	}

	public Date getDue_date() {
		return due_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public Blob getUpload_file() {
		return upload_file;
	}

	public void setUpload_file(Blob upload_file) {
		this.upload_file = upload_file;
	}

	public String getUpload_file_url() {
		return upload_file_url;
	}

	public void setUpload_file_url(String upload_file_url) {
		this.upload_file_url = upload_file_url;
	}

	public Blob getReturn_file() {
		return return_file;
	}

	public void setReturn_file(Blob return_file) {
		this.return_file = return_file;
	}

	public String getReturn_file_url() {
		return return_file_url;
	}

	public void setReturn_file_url(String return_file_url) {
		this.return_file_url = return_file_url;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Date getRerturn_date() {
		return return_date;
	}

	public void setRerturn_date(Date rerturn_date) {
		this.return_date = rerturn_date;
	}

	public String getTrans_type() {
		return trans_type;
	}

	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}

	public Date getDue_date_3() {
		return due_date_3;
	}

	public void setDue_date_3(Date due_date_3) {
		this.due_date_3 = due_date_3;
	}

	public Date getReturn_date() {
		return return_date;
	}

	public void setReturn_date(Date return_date) {
		this.return_date = return_date;
	}

	public String getUpload_file_name() {
		return upload_file_name;
	}

	public void setUpload_file_name(String upload_file_name) {
		this.upload_file_name = upload_file_name;
	}

	public String getReturn_file_name() {
		return return_file_name;
	}

	public void setReturn_file_name(String return_file_name) {
		this.return_file_name = return_file_name;
	}
	public int getNumber_of_letters() {
		return number_of_letters;
	}

	public void setNumber_of_letters(int number_of_letters) {
		this.number_of_letters = number_of_letters;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getVoulenteer_account() {
		return voulenteer_account;
	}

	public void setVoulenteer_account(String voulenteer_account) {
		this.voulenteer_account = voulenteer_account;
	}
	
	public String getCountry_name() {
		return country_name;
	}

	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}

	public String getCountry_id() {
		return country_id;
	}

	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}
	
	public String getLetter_source_type() {
		return letter_source_type;
	}

	public void setLetter_source_type(String letter_source_type) {
		this.letter_source_type = letter_source_type;
	}

	public Integer getReturn_days() {
		return return_days;
	}

	public void setReturn_days(Integer return_days) {
		this.return_days = return_days;
	}
	

}
