package com.simon.apiconnect.domain.bundle;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
	private LocalDateTime lastLogged;
	private String accountManager;
	private LocalDate movedToSupport;
	private LocalDate bundleStarts;
	private LocalDate bundleEnds;
	private int bundleSize;
	private LocalDate lastUpdated;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	private Set<TimeCorrection> corrections = new HashSet<>();
	
	public Organisation() {}

	/* getters and setters */
	public long getZendeskId() {
		return zendeskId;
	}
	public void setZendeskId(long zendeskId) {
		this.zendeskId = zendeskId;
	}
	public LocalDateTime getLastLogged() {
		return lastLogged;
	}
	public void setLastLogged(LocalDateTime lastLogged) {
		this.lastLogged = lastLogged;
	}
	public String getAccountManager() {
		return accountManager;
	}
	public void setAccountManager(String accountManager) {
		this.accountManager = accountManager;
	}
	public LocalDate getMovedToSupport() {
		return movedToSupport;
	}
	public void setMovedToSupport(LocalDate movedToSupport) {
		this.movedToSupport = movedToSupport;
	}
	public LocalDate getBundleStarts() {
		return bundleStarts;
	}
	public void setBundleStarts(LocalDate bundleStarts) {
		this.bundleStarts = bundleStarts;
	}
	public LocalDate getBundleEnds() {
		return bundleEnds;
	}
	public void setBundleEnds(LocalDate bundleEnds) {
		this.bundleEnds = bundleEnds;
	}
	public int getBundleSize() {
		return bundleSize;
	}
	public void setBundleSize(int bundleSize) {
		this.bundleSize = bundleSize;
	}
	public LocalDate getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Set<TimeCorrection> getCorrections() {
		return corrections;
	}
	public void setCorrections(Set<TimeCorrection> corrections) {
		this.corrections = corrections;
	}
}