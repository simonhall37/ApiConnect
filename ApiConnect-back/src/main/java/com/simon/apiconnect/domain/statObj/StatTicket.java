package com.simon.apiconnect.domain.statObj;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.simon.apiconnect.domain.bundle.Ticket;

@Entity
@Table(name="ticket")
public class StatTicket {

	@Id
	@GeneratedValue
	private long id;
	
//	private Ticket zenTicket;
	private double totalEffort;
	
	public StatTicket() {}
	
	/* getters and setters */
//	public Ticket getZenTicket() {
//		return zenTicket;
//	}
//	public void setZenTicket(Ticket zenTicket) {
//		this.zenTicket = zenTicket;
//	}
	public double getTotalEffort() {
		return totalEffort;
	}
	public void setTotalEffort(double totalEffort) {
		this.totalEffort = totalEffort;
	}
	
	
}
