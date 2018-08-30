package com.simon.apiconnect.domain.bundle;

import java.util.ArrayList;
import java.util.List;

public class Org {

	private long id;
	private String name;
	private String type;
	
	public Org() {}
	
	public Org(long id) {
		this.id = id;
	}

	public String toString() {
		return this.id + ":" + this.name + " --- " + this.type;
	}
	
	public String generateHeader() {
		return wrapinQuotes("id",false) + 
				wrapinQuotes("name",true);
	}
	
	public String wrapinQuotes(String input,boolean last) {
		if (last) 
			return "\"" + input + "\"";
		else return "\"" + input + "\",";
	}
	
	public List<Object> generateContent() {
		List<Object> out = new ArrayList<>();
		out.add(this.id);
		out.add(this.name);
		return out;
	}
	
	/* getters and setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
