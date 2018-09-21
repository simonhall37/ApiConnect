package com.simon.apiconnect.domain.bundle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

public class TimeCorrection {

	@Id
	@GeneratedValue
	private long id;
	
	private long ticketId;
	private double newEffort;
	
	public TimeCorrection() {}
	
	public TimeCorrection(long ticketId,double newEffort) {
		this.ticketId = ticketId;
		this.newEffort = newEffort;
	}
	
	public String toString() {
		return this.id + "," + this.ticketId + "," + this.newEffort;
	}
	
	/*getters and setters */
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
	public double getNewEffort() {
		return newEffort;
	}
	public void setNewEffort(double newEffort) {
		this.newEffort = newEffort;
	}
	
}
