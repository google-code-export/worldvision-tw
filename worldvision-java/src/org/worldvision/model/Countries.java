package org.worldvision.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Countries {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private Blob note;
	@Persistent
	private Blob template;
	@Persistent
	private Blob noun;
	@Persistent
	private String note_url;
	@Persistent
	private String noun_url;
	@Persistent
	private String template_url;
	@Persistent
	private String background_url;
	@Persistent
	private String note_file_name;
	@Persistent
	private String noun_file_name;
	@Persistent
	private String template_file_name;
	@Persistent
	private String background_file_name;
	public Countries(Blob content) {
		this.note = content;
	}
	public Key getId() {
		return id;
	}
	public void setId(Key id) {
		this.id = id;
	}
	public Blob getNote() {
		return note;
	}
	public void setNote(Blob note) {
		this.note = note;
	}
	public String getNote_url() {
		return note_url;
	}
	public void setNote_url(String note_url) {
		this.note_url = note_url;
	}
	public String getNoun_url() {
		return noun_url;
	}
	public void setNoun_url(String noun_url) {
		this.noun_url = noun_url;
	}
	public String getTemplate_url() {
		return template_url;
	}
	public void setTemplate_url(String template_url) {
		this.template_url = template_url;
	}
	public Blob getTemplate() {
		return template;
	}
	public void setTemplate(Blob template) {
		this.template = template;
	}
	public Blob getNoun() {
		return noun;
	}
	public void setNoun(Blob noun) {
		this.noun = noun;
	}
	public String getNote_file_name() {
		return note_file_name;
	}
	public void setNote_file_name(String note_file_name) {
		this.note_file_name = note_file_name;
	}
	public String getNoun_file_name() {
		return noun_file_name;
	}
	public void setNoun_file_name(String noun_file_name) {
		this.noun_file_name = noun_file_name;
	}
	public String getTemplate_file_name() {
		return template_file_name;
	}
	public void setTemplate_file_name(String template_file_name) {
		this.template_file_name = template_file_name;
	}
	public String getBackground_url() {
		return background_url;
	}
	public void setBackground_url(String background_url) {
		this.background_url = background_url;
	}
	public String getBackground_file_name() {
		return background_file_name;
	}
	public void setBackground_file_name(String background_file_name) {
		this.background_file_name = background_file_name;
	}

}
