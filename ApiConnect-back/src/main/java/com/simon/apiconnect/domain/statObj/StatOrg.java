package com.simon.apiconnect.domain.statObj;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="organisation")
public class StatOrg {

	@Id
	@GeneratedValue
	private long id;
	
	private long zendeskId;
	private String orgName;
	private String lastLogged;
	private String accountManager;
	private String movedToSupport;
	private String bundleStarts;
	private String bundleEnds;
	private int bundleSize;
	private double currentBalance;
	private boolean unlimited;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
	private Set<StatBundle> bundles = new HashSet<>();
	
	public StatOrg() {}
	
	public StatOrg(String zendeskId,String accountManager,String movedToSupport,String bundleStarts,String bundleEnds,String bundleSize, String unlimited) throws ClassCastException{
		try{
			this.zendeskId = Long.parseLong(zendeskId);
			this.accountManager = accountManager;
			this.movedToSupport = movedToSupport;
			this.bundleStarts = bundleStarts;
			this.bundleEnds = bundleEnds;
			if (unlimited.equalsIgnoreCase("TRUE"))
				this.unlimited = true;
			else if (unlimited.equalsIgnoreCase("FALSE"))
				this.unlimited = false;
			else throw new ClassCastException("Expecting TRUE/FALSE for unlimied but received " + unlimited);
			this.bundleSize = Integer.parseInt(bundleSize);
		} catch (ClassCastException e) {
			throw new ClassCastException(e.getMessage());
		}
		
	}
	
	public StatOrg addBundles(List<StatBundle> bundles) {
		this.bundles.addAll(bundles);
		return this;
	}
	
	/* getters and setters */
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getZendeskId() {
		return zendeskId;
	}
	public void setZendeskId(long zendeskId) {
		this.zendeskId = zendeskId;
	}
	public String getLastLogged() {
		return lastLogged;
	}
	public void setLastLogged(String lastLogged) {
		this.lastLogged = lastLogged;
	}
	public String getAccountManager() {
		return accountManager;
	}
	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}
	public String getMovedToSupport() {
		return movedToSupport;
	}
	public void setMovedToSupport(String movedToSupport) {
		this.movedToSupport = movedToSupport;
	}
	public String getBundleStarts() {
		return bundleStarts;
	}
	public void setBundleStarts(String bundleStarts) {
		this.bundleStarts = bundleStarts;
	}
	public String getBundleEnds() {
		return bundleEnds;
	}
	public void setBundleEnds(String bundleEnds) {
		this.bundleEnds = bundleEnds;
	}
	public int getBundleSize() {
		return bundleSize;
	}
	public void setBundleSize(int bundleSize) {
		this.bundleSize = bundleSize;
	}
	public double getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
	public Set<StatBundle> getBundles(){
		return this.bundles;
	}
	public void setBundles(Set<StatBundle> bundles) {
		this.bundles = bundles;
	}
	public boolean isUnlimited() {
		return unlimited;
	}
	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
