package com.simon.apiconnect.domain.bundle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ExtraTicket {

	@GeneratedValue
	@Id
	private int id;
	
	private String lastUpdated;
	private double newEffort;
	private String subject;
	
	public ExtraTicket() {}
	
	public ExtraTicket(String subject, String lastUpdated,double newEffort) {
		this.subject = subject;
		this.lastUpdated = lastUpdated;
		this.newEffort = newEffort;
	}
	
	public double getNewEffort() {
		return newEffort;
	}
	public void setNewEffort(double newEffort) {
		this.newEffort = newEffort;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
