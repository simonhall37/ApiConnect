package com.simon.apiconnect.domain.cache;

public class Pair {
	private String key;
	private String value;
	
	public Pair() {}
	
	public Pair(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
