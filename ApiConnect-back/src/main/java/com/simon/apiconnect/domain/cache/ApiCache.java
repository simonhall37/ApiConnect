package com.simon.apiconnect.domain.cache;

import java.util.ArrayList;
import java.util.List;

public class ApiCache {

	private ApiCacheSummary summary;
	private List<Object> content;
	
	public ApiCache() {
		this.content = new ArrayList<>();
	}

	public ApiCache(ApiCacheSummary summary) {
		this();
		this.summary = summary;
	}
	
	/* getters and setters */
	public ApiCacheSummary getSummary() {
		return summary;
	}

	public void setSummary(ApiCacheSummary summary) {
		this.summary = summary;
	}

	public List<Object> getContent() {
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

}
