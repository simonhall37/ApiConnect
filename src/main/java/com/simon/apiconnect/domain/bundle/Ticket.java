package com.simon.apiconnect.domain.bundle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Comparable<Ticket> {

	private long id;
	private String subject;
	private long requesterId;
	private long organisationId;
	private String type;
	private String priority;
	private String created;
	private String updated;
	private String status;
	private double effort;
	private String division;

	public Ticket() {
	}

	public String getHeader() {
		return wrapinQuotes("id",false) + 
				 wrapinQuotes("subject",false) +
				 wrapinQuotes("organisation",false) +
				 wrapinQuotes("requesterId",false) +
				 wrapinQuotes("type",false) +
				 wrapinQuotes("priority",false) +
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
	
	public List<Object> getObj() {
		List<Object> out = new ArrayList<>();
		out.add(this.id);
		out.add(this.subject);
		out.add(this.organisationId);
		out.add(this.requesterId);
		out.add(this.type);
		out.add(priority);
		out.add(this.created);
		out.add(this.updated);
		out.add(this.status);
		out.add(this.effort);
		out.add(Math.round(this.effort * 100.0 / 60.0) / 100.0);
		return out;
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

	public long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(long requesterId) {
		this.requesterId = requesterId;
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

	public long getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(long organisationId) {
		this.organisationId = organisationId;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
	
}
