package com.simon.apiconnect.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="apiconnection")
public class ApiConnection {

	@Id
	@GeneratedValue
	private long id;
	private String name;
	private String baseURL;
	private CredentialType type;
	private String credKey;
	private String credValue;
	
	public ApiConnection () {}
	
	public ApiConnection (String name, String baseURL, CredentialType type, String credKey, String credValue){
		this.name = name;
		this.baseURL = baseURL;
		this.type = type;
		this.credKey = credKey;
		this.credValue = credValue;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public CredentialType getType() {
		return type;
	}

	public void setType(CredentialType type) {
		this.type = type;
	}

	public String getCredKey() {
		return credKey;
	}

	public void setCredKey(String credKey) {
		this.credKey = credKey;
	}

	public String getCredValue() {
		return credValue;
	}

	public void setCredValue(String credValue) {
		this.credValue = credValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
