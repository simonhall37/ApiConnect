package com.simon.apiconnect.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;
import com.simon.apiconnect.domain.cache.ApiCache;
import com.simon.apiconnect.domain.cache.SourceType;
import com.simon.apiconnect.domain.transformers.Page;
import com.simon.apiconnect.domain.transformers.Transformer;
import com.simon.apiconnect.domain.transformers.TransformerOrg;
import com.simon.apiconnect.domain.transformers.TransformerTicket;
import com.simon.apiconnect.domain.transformers.TransformerUser;

@Service
public class ConnectionService {

	private RestTemplate restTemplate;

	private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);

	@Autowired
	private RestTemplateBuilder builder;
	
	public void init() {

		if (this.restTemplate == null)
			this.restTemplate = builder.build();

	}
	
	public void cache(ApiCache cache, ApiConnection connection) {		
		
		Page page = new Page(cache.getSummary().getSource().toString().toLowerCase());
		Transformer trans = null;
		if (cache.getSummary().getSource().equals(SourceType.ORGANIZATIONS))
			trans = new TransformerOrg(cache.getSummary().getFilter());
		else if (cache.getSummary().getSource().equals(SourceType.TICKETS))
			trans = new TransformerTicket(cache.getSummary().getFilter());
		else if (cache.getSummary().getSource().equals(SourceType.USERS))
			trans = new TransformerUser(cache.getSummary().getFilter());
		boolean initial=true;
		
		try {
			int check=0;
			while (page.getNextPage()!=null && ++check<350) {
				String url = null;
				if (initial) url = cache.getSummary().getPath() + cache.getSummary().getParamString();
				else url = cache.getSummary().getPath() + "?";
				page.parse(Connect(connection, url + page.getParams()));
				cache.getContent().addAll(trans.transform(page));
				log.info("Current size: " + cache.getContent().size() + " with next page " + page.getNextPage());
				initial=false;
			}
		} catch (JsonParseException | JsonMappingException e) {
			log.error("Error parsing JSON when reading data to the cache: " + e.getMessage() + " --- " + e.getCause());
		} catch (IOException e) {
			log.error("IO Exception when reading data to the cache: " + e.getMessage() + " --- " + e.getCause());
		}
		
		cache.getSummary().setSize(cache.getContent().size());
		cache.getSummary().setUpdatedOn(LocalDateTime.now().toString());
		
	}

	@SuppressWarnings("serial")
	public String Connect(ApiConnection connection, String address) {

		init();
		
		HttpEntity<String> response = null;
		HttpHeaders headers = null;

		if (connection.getType().equals(CredentialType.TOKEN)) {
			headers = new HttpHeaders() {
				{
					set(connection.getCredKey(), connection.getCredValue());
				}
			};
		} else if (connection.getType().equals(CredentialType.BASIC)) {
			headers = new HttpHeaders() {
				{
					set("Authorization", basicAuthHeader(connection.getCredKey(), connection.getCredValue()));
				}
			};
		}

		response = restTemplate.exchange(connection.getBaseURL() + address, HttpMethod.GET,
				new HttpEntity<String>(headers), String.class);

		return response.getBody();

	}

	public String basicAuthHeader(String username, String password) {
		String auth = username + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(encodedAuth);
	}

}
