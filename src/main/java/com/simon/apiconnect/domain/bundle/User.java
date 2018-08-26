package com.simon.apiconnect.domain.bundle;

public class User {

	private long id;
	private String name;
	
	public User() {}
	
	public String toString() {
		return this.id + "|" + this.name;
	}
	
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
