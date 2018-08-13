package com.simon.apiconnect.services;

import java.nio.charset.Charset;

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

import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;

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

	@SuppressWarnings("serial")
	public String Connect(ApiConnection connection, String address) {

		init();
		log.info("Trying to connect to: " + connection.getBaseURL() + address);

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
