package com.simon.apiconnect.domain.statObj;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="bundle")
public class StatBundle implements Comparable<StatBundle> {

	@Id
	@GeneratedValue
	private long id;
	
	private String startDate;
	private String endDate;
	private double balance;
	private long firstTicketId;
	private long lastTicketId;
	private long orgZenId;
	private int bundleNum;
	private int bundleSize;
	private boolean active;
	private String orgName;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
	@javax.persistence.OrderBy("createdDateTime")
	private SortedSet<StatTicket> tickets = new TreeSet<>();
	
	public StatBundle() {}
	
	public StatBundle(String bundleNum, String startDate, String endDate, String firstTicketId, String lastTicketId, String orgZenId, String active, String bundleSize) throws ClassCastException{
		try {
			this.startDate = startDate;
			this.endDate = endDate;
			try{
				this.bundleNum = Integer.parseInt(bundleNum);
			} catch (NumberFormatException e) {
				throw new ClassCastException("Can't parse bundle num from " + bundleNum);
			}
			try{
				this.firstTicketId = Long.parseLong(firstTicketId);
			} catch (NumberFormatException e) {	}
			try{
				this.lastTicketId = Long.parseLong(lastTicketId);
			} catch (NumberFormatException e) {}
			try {
				this.orgZenId = Long.parseLong(orgZenId);
			} catch (NumberFormatException e) {
				throw new ClassCastException("Can't parse zendesk id from " + orgZenId);
			}
			try {
				this.bundleSize = Integer.parseInt(bundleSize);
			} catch (NumberFormatException e) {
				throw new ClassCastException("Can't parse bundle size from " + bundleSize);
			}
			if (active.equalsIgnoreCase("TRUE"))
				this.active = true;
			else if (active.equalsIgnoreCase("FALSE"))
				this.active = false;
			else throw new ClassCastException("Can't parse boolean from " + active);
		} catch (ClassCastException e) {
			throw new ClassCastException(e.getMessage());
		} 
	}
	
	public void wipeTickets() {
		this.tickets.removeIf(t -> t.getZenTicketId()!=0L);
		restoreBalance();
	}
	
	public void addTicket(StatTicket ticket) {
		this.tickets.add(ticket);
		ticket.setBundleNum(this.getBundleNum());
	}
	
	public void restoreBalance() {
		this.balance = 0;
	}
	
	public void updateBalance(StatTicket ticket) {
		this.balance = this.balance + Math.round(100*ticket.getTotalEffort()/60)/100.0;
//		System.out.println(this.orgName + " (" + ticket.getZenTicketId() + ") : " + this.balance + " --- " + ticket.getTotalEffort());
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

	public int getBundleNum() {
		return bundleNum;
	}

	public void setBundleNum(int bundleNum) {
		this.bundleNum = bundleNum;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getBundleSize() {
		return bundleSize;
	}

	public void setBundleSize(int bundleSize) {
		this.bundleSize = bundleSize;
	}

	@Override
	public int compareTo(StatBundle arg0) {
		return  this.bundleNum - arg0.getBundleNum();
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

}
