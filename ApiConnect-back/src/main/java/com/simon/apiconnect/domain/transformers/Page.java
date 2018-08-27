package com.simon.apiconnect.domain.transformers;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Page {

	private String nextPageKey = "next_page";
	private String nextPage;
	private String contentWrapper;
	private JsonNode content;
	
	public Page(String contentWrapper) {
		this.nextPage = "";
		this.contentWrapper = contentWrapper;
	}
	
	public void parse(String input) throws IOException {
		ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.content = om.readTree(input);
		setNextPage(this.content.get(this.nextPageKey).asText());
	}
	
	public String getParams() {
		if (nextPage!=null && nextPage.contains("?"))
			return this.nextPage.substring(this.nextPage.indexOf("?")+1);
		else return "";
	}
	
	/* getters and setters */
	public String getNextPage() {
		return this.nextPage;
	}

	public void setNextPage(String nextPage) {
		if (!nextPage.equalsIgnoreCase("null"))
			this.nextPage = nextPage;
		else this.nextPage=null;
	}

	public String getNextPageKey() {
		return nextPageKey;
	}

	public void setNextPageKey(String nextPageKey) {
		this.nextPageKey = nextPageKey;
	}

	public String getContentWrapper() {
		return contentWrapper;
	}

	public void setContentWrapper(String contentWrapper) {
		this.contentWrapper = contentWrapper;
	}

	public JsonNode getContent() {
		return content;
	}

	public void setContent(JsonNode content) {
		this.content = content;
	}
}

