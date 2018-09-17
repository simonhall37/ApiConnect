package com.simon.apiconnect.domain.statObj;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="bundle")
public class StatBundle {

	@Id
	@GeneratedValue
	private long id;
	
	private String startDate;
	private String endDate;
	private double balance;
	private long firstTicketId;
	private long lastTicketId;
	private long OrgZenId;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	private Set<StatTicket> tickets = new HashSet<>();
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	private Set<StatCorrection> corrections = new HashSet<>();
	
	public StatBundle() {}
	
	/* getters and setters */
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public long getFirstTicketId() {
		return firstTicketId;
	}
	public void setFirstTicketId(long firstTicketId) {
		this.firstTicketId = firstTicketId;
	}
	public long getLastTicketId() {
		return lastTicketId;
	}
	public void setLastTicketId(long lastTicketId) {
		this.lastTicketId = lastTicketId;
	}
	public long getOrgZenId() {
		return OrgZenId;
	}
	public void setOrgZenId(long orgZenId) {
		OrgZenId = orgZenId;
	}
	public Set<StatTicket>  getTickets(){
		return this.tickets;
	}
	public void setTickets(Set<StatTicket>  tickets) {
		this.tickets = tickets;
	}
	public Set<StatCorrection>  getCorrections(){
		return this.corrections;
	}
	public void setcorrections(Set<StatCorrection>  corrections) {
		this.corrections = corrections;
	}
}
