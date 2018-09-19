package com.simon.apiconnect.domain.statObj;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="bundle")
public class StatBundle {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String startDate;
	private String endDate;
	private double balance;
	private long firstTicketId;
	private long lastTicketId;
	private long orgZenId;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
	@javax.persistence.OrderBy("createdDateTime")
	private SortedSet<StatTicket> tickets = new TreeSet<>();
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
	private Set<StatCorrection> corrections = new HashSet<>();
	
	public StatBundle() {}
	
	public StatBundle(String startDate, String endDate, String firstTicketId, String lastTicketId, String orgZenId) throws ClassCastException{
		try {
			this.startDate = startDate;
			this.endDate = endDate;
			try{
				this.firstTicketId = Long.parseLong(firstTicketId);
			} catch (NumberFormatException e) {}
			try{
				this.lastTicketId = Long.parseLong(lastTicketId);
			} catch (NumberFormatException e) {}
			try {
				this.orgZenId = Long.parseLong(orgZenId);
			} catch (NumberFormatException e) {}
		} catch (ClassCastException e) {
			throw new ClassCastException(e.getMessage());
		} 
	}
	
	public void addTicket(StatTicket ticket) {
		this.tickets.add(ticket);
	}
	
	/* getters and setters */
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
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
		return orgZenId;
	}
	public void setOrgZenId(long orgZenId) {
		this.orgZenId = orgZenId;
	}
	public SortedSet<StatTicket>  getTickets(){
		return this.tickets;
	}
	public void setTickets(SortedSet<StatTicket>  tickets) {
		this.tickets = tickets;
	}
	public Set<StatCorrection>  getCorrections(){
		return this.corrections;
	}
	public void setcorrections(SortedSet<StatCorrection>  corrections) {
		this.corrections = corrections;
	}
}
