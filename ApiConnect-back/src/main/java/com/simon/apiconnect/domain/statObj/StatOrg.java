package com.simon.apiconnect.domain.statObj;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "organisation")
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
	private int currentBundleId;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<StatBundle> bundles = new HashSet<>();
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<StatCorrection> corrections = new HashSet<>();

	public StatOrg() {
	}

	public StatOrg(String zendeskId, String accountManager, String movedToSupport, String bundleStarts,
			String bundleEnds, String unlimited) throws ClassCastException {
		try {
			this.zendeskId = Long.parseLong(zendeskId);
			this.accountManager = accountManager;
			this.movedToSupport = movedToSupport;
			this.bundleStarts = bundleStarts;
			this.bundleEnds = bundleEnds;
			if (unlimited.equalsIgnoreCase("TRUE"))
				this.unlimited = true;
			else if (unlimited.equalsIgnoreCase("FALSE"))
				this.unlimited = false;
			else
				throw new ClassCastException("Expecting TRUE/FALSE for unlimied but received " + unlimited);
		} catch (ClassCastException e) {
			throw new ClassCastException(e.getMessage());
		}

	}
	
	public StatBundle findLatestBundle() {
		StatBundle latest = null;
		for (StatBundle b : this.bundles) {
			if (b.getActive())
				if (latest == null || latest.getBundleNum() < b.getBundleNum())
					latest = b;
		}
		return latest;
	}

	public StatOrg addBundles(List<StatBundle> bundles) {
		this.bundles.addAll(bundles);
		return this;
	}

	public StatOrg addCorrections(List<StatCorrection> corrections) {
		if (corrections != null)
			this.corrections.addAll(corrections);
		return this;
	}

	public StatOrg addExtraTickets(List<StatTicket> extra) {
		int counter = extra!=null?extra.size():0;
		if (counter > 0) {
			for (StatTicket e : extra) {
				for (StatBundle bundle : this.bundles) {
					LocalDate extraDate = LocalDateTime.parse(e.getCreatedDateTime()).toLocalDate();
					LocalDate bundleStart = LocalDate.parse(bundle.getStartDate());
					LocalDate bundleEnd = LocalDate.parse(bundle.getEndDate());

					if (extraDate.isAfter(bundleStart) || extraDate.isEqual(bundleStart)) {
						if (extraDate.isBefore(bundleEnd) || extraDate.isEqual(bundleEnd)) {
							bundle.addTicket(e);
							counter--;
							break;
						}
					}
				}
			}
			if (counter>0)System.out.println("Couldn't match " + counter + " tickets for " + this.zendeskId);
		}

		return this;
	}
	
	public boolean updateOrgDetails() {
		StatBundle latest = findLatestBundle();
		this.bundleStarts = latest.getStartDate();
		this.bundleEnds = latest.getEndDate();
		this.currentBalance = Math.round(100*(this.bundleSize - latest.getBalance()/60))/100.0;
		this.currentBundleId = latest.getBundleNum();
		this.bundleSize = latest.getBundleSize();
		return true;
	}

	public void applyCorrections() {
		Map<Long,Double> toCorrect = new HashMap<>();
		for (StatCorrection c : this.corrections) {
			toCorrect.put(c.getZenTicketId(), c.getUpdatedHours());
		}
		for (StatBundle bundle : this.bundles) {
			for (StatTicket t : bundle.getTickets()) {
				if (toCorrect.get(t.getZenTicketId())!=null) {
					bundle.setBalance(bundle.getBalance() - Math.round(100*t.getTotalEffort()/60)/100.0);
					t.setTotalEffort(toCorrect.get(t.getZenTicketId()));
					bundle.setBalance(bundle.getBalance() + Math.round(100*t.getTotalEffort()/60)/100.00);
//					System.out.println("Corrected " + t.getZenTicketId());
				}
			}
		}
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

	public Set<StatBundle> getBundles() {
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

	public Set<StatCorrection> getCorrections() {
		return this.corrections;
	}

	public void setcorrections(Set<StatCorrection> corrections) {
		this.corrections = corrections;
	}

	public int getCurrentBundleId() {
		return currentBundleId;
	}

	public void setCurrentBundleId(int currentBundleId) {
		this.currentBundleId = currentBundleId;
	}
}
