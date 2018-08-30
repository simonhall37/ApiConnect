package com.simon.apiconnect.domain.bundle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="time_correction")
public class TimeCorrection {

	@Id
	@GeneratedValue
	private long id;
	
	private long zendeskId;
	private int newEffort;
	
	public TimeCorrection() {}
	
	/*getters and setters */
	public long getZendeskId() {
		return zendeskId;
	}
	public void setZendeskId(long zendeskId) {
		this.zendeskId = zendeskId;
	}
	public int getNewEffort() {
		return newEffort;
	}
	public void setNewEffort(int newEffort) {
		this.newEffort = newEffort;
	}
	
}
