package com.simon.apiconnect.domain.bundle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Ticket implements Comparable<Ticket> {

	private long id;
	private String subject;
	private User requester;
	private Org organisation;
	private String type;
	private String priority;
	private String created;
	private String updated;
	private String status;
	private double effort;
	private String division;

	public Ticket() {
	}
	
	public Ticket addUser(User user) {
		this.requester = user;
		return this;
	}
	
	public Ticket addOrg(Org org) {
		setOrganisation(org);
		return this;
	}

	public String generateHeader() {
		return wrapinQuotes("id",false) + 
				 wrapinQuotes("subject",false) +
				 wrapinQuotes("organisation",false) +
				 wrapinQuotes("requester",false) +
				 wrapinQuotes("type",false) +
				 wrapinQuotes("created",false) +
				 wrapinQuotes("updated",false) +
				 wrapinQuotes("status",false) +
				 wrapinQuotes("effort (min)",false) +
				 wrapinQuotes("effort (hours)",true)
				;
	}
	
	public String wrapinQuotes(String input,boolean last) {
		if (last) 
			return "\"" + input + "\"";
		else return "\"" + input + "\",";
	}
	
	public List<Object> generateContent(Set<TimeCorrection> corrections) {
		List<Object> out = new ArrayList<>();
		out.add(this.id);
		out.add(this.subject);
		out.add(this.organisation.getName());
		out.add(this.requester.getName());
		out.add(this.type);
		out.add(this.created);
		out.add(this.updated);
		out.add(this.status);
		double temp = correctTime(corrections);
		out.add(temp);
		out.add(Math.round(temp * 100.0 / 60.0) / 100.0);
		return out;
	}
	
	public Double correctTime(Set<TimeCorrection> corrections) {
		if (corrections!=null) {
			for (TimeCorrection tc : corrections) {
				if (tc.getTicketId() == this.id) {
					System.out.println("Correcting ticket " + id);
					return tc.getNewEffort();
				}
			}
		}
		return this.effort;
	}
	
	@Override
	public int compareTo(Ticket o) {
		return LocalDateTime.parse(this.created).compareTo(LocalDateTime.parse(o.created));
	}
	
	/* getters and setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getEffort() {
		return effort;
	}

	public void setEffort(double effort) {
		this.effort = effort;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public Org getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Org organisation) {
		this.organisation = organisation;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}
	
}
