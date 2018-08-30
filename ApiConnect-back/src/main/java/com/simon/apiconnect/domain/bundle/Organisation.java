package com.simon.apiconnect.domain.bundle;

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
@Table(name="organisation")
public class Organisation {

	@Id
	@GeneratedValue
	private long id;
	
	private long zendeskId;
	private String lastLogged;
	private String accountManager;
	private String movedToSupport;
	private String bundleStarts;
	private String bundleEnds;
	private int bundleSize;
	private String lastUpdated;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	private Set<TimeCorrection> corrections = new HashSet<>();
	
	public Organisation() {}
	
	public Organisation(long id,long zendeskId,String bundleStarts) {
		this.id = id;
		this.zendeskId = zendeskId;
		this.bundleStarts = bundleStarts;
	}

	/* getters and setters */
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
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Set<TimeCorrection> getCorrections() {
		return corrections;
	}
	public void setCorrections(Set<TimeCorrection> corrections) {
		this.corrections = corrections;
	}
}