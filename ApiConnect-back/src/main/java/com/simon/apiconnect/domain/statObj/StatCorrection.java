package com.simon.apiconnect.domain.statObj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="correction")
public class StatCorrection {

	@Id
	@GeneratedValue
	private long id;
	
	private long zenTicketId;
	private double updatedHours;
	
	public StatCorrection() {}
	
	/* getters and setters */
	public long getZenTicketId() {
		return zenTicketId;
	}
	public void setZenTicketId(long zenTicketId) {
		this.zenTicketId = zenTicketId;
	}
	public double getUpdatedHours() {
		return updatedHours;
	}
	public void setUpdatedHours(double updatedHours) {
		this.updatedHours = updatedHours;
	}
	
	
	
}
