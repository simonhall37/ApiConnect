package com.simon.apiconnect.domain.statObj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="corr")
public class StatCorrection {

	@Id
	@GeneratedValue
	private long id;
	
	private long zenOrgId;
	private long zenTicketId;
	private double updatedHours;
	
	public StatCorrection() {}
	
	public StatCorrection(String zenOrgId, String zenTicketId, String updatedHours) {
		if (zenOrgId.length() > 0 && zenTicketId.length()>0 && updatedHours.length()>0) {
			this.zenOrgId = Long.parseLong(zenOrgId);
			this.zenTicketId = Long.parseLong(zenTicketId);
			this.updatedHours = Long.parseLong(updatedHours);
		}
	}
	
	/* getters and setters */
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
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

	public long getZenOrgId() {
		return zenOrgId;
	}

	public void setZenOrgId(long zenOrgId) {
		this.zenOrgId = zenOrgId;
	}
	
	
	
}
