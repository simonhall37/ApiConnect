package com.simon.apiconnect.domain.statObj;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.simon.apiconnect.domain.bundle.Ticket;

@Entity
@Table(name="ticket")
public class StatTicket implements Comparable<StatTicket> {

	@Id
	@GeneratedValue
	private long id;
	
	private long zenTicketId;
	private double totalEffort;
	private String createdDateTime;
	private String requesterName;
	private String subject;
	private String type;
	private String status;
	
	public StatTicket() {}
	
	public StatTicket(Ticket ticket) {
		this.zenTicketId = ticket.getId();
		this.totalEffort = ticket.getEffort();
		this.createdDateTime = ticket.getCreated();
		this.requesterName = ticket.getRequester().getName();
		this.subject = ticket.getSubject();
		this.type = ticket.getType();
		this.status = ticket.getStatus();
	}
	
	@Override
	public int compareTo(StatTicket o) {
		return LocalDateTime.parse(this.createdDateTime).compareTo(LocalDateTime.parse(o.createdDateTime));
	}

	/* getters and setters */
	public double getTotalEffort() {
		return totalEffort;
	}
	public void setTotalEffort(double totalEffort) {
		this.totalEffort = totalEffort;
	}

	public long getZenTicketId() {
		return zenTicketId;
	}

	public void setZenTicketId(long zenTicketId) {
		this.zenTicketId = zenTicketId;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
