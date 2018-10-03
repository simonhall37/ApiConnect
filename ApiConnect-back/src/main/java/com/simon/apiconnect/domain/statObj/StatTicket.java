package com.simon.apiconnect.domain.statObj;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.simon.apiconnect.domain.bundle.Ticket;

@Entity
@Table(name = "ticket")
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
	private long zenOrgId;
	private int bundleNum;
	private String orgName;

	public StatTicket() {
	}

	public StatTicket(String zenOrgId) {
	}

	public StatTicket(Ticket ticket) {
		this.zenTicketId = ticket.getId();
		this.totalEffort = ticket.getEffort();
		this.zenOrgId = ticket.getOrganisation().getId();
		this.createdDateTime = ticket.getCreated();
		try{
			this.requesterName = ticket.getRequester().getName();
		} catch (NullPointerException e) {
			System.out.println("Error: " + ticket.getId() + " has null requester");
		}
		this.subject = ticket.getSubject();
		this.type = ticket.getType();
		this.status = ticket.getStatus();
	}
	
	public StatTicket updateOrgName(String orgName) {
		this.orgName = orgName;
		return this;
	}

	public StatTicket(String zenOrgId, String subject, String createdDateTime, String totalEffort) {
		this.zenOrgId = Long.parseLong(zenOrgId);
		this.subject = subject;
		this.createdDateTime = createdDateTime;
		this.totalEffort = Double.parseDouble(totalEffort);
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

	public long getZenOrgId() {
		return this.zenOrgId;
	}

	public void setZenOrgId(long zenOrgId) {
		this.zenOrgId = zenOrgId;
	}

	public int getBundleNum() {
		return bundleNum;
	}

	public void setBundleNum(int bundleNum) {
		this.bundleNum = bundleNum;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
