package com.simon.apiconnect.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;
import com.simon.apiconnect.domain.Profile;
import com.simon.apiconnect.domain.cache.ApiCache;
import com.simon.apiconnect.domain.cache.ApiCacheSummary;
import com.simon.apiconnect.services.CacheRepository;
import com.simon.apiconnect.services.ConnectionService;
import com.simon.apiconnect.services.ProfileRepository;

@RestController
@RequestMapping(value = "/api/cache")
public class CacheController {

	@Autowired
	private ConnectionService conService;
	
	@Autowired
	private CacheRepository cacheRepo;
	
	@Autowired
	private ProfileRepository profileRepo;
	
	private static final Logger log = LoggerFactory.getLogger(CacheController.class);
	
	private Profile getDefaultProfile() {
		Profile defaultProfile = null;
		
		try {
			
			defaultProfile = profileRepo.findByName("default").get();
			
		} catch (NoSuchElementException e) {
			
			defaultProfile = new Profile(1,"default");
			defaultProfile.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
			defaultProfile.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));

			profileRepo.save(defaultProfile);
		}

		return defaultProfile;
	}
	
	@GetMapping
	@RequestMapping(value = "/{name}/summary")
	public ResponseEntity<ApiCacheSummary> getSummary(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			ApiCacheSummary summary = this.cacheRepo.getByName(name, true).getSummary();
			return new ResponseEntity<>(summary, HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for summary request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@GetMapping
	@RequestMapping(value = "/{name}/content")
	public ResponseEntity<List<Object>> getContent(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			List<Object> content = this.cacheRepo.getByName(name, true).getContent();
			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for content request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@PostMapping
	public ResponseEntity<List<ApiCacheSummary>> cache(@RequestBody List<ApiCacheSummary> summaries) throws JsonParseException, JsonMappingException, IOException {
		
		getDefaultProfile();
		
		StringBuilder names = new StringBuilder();
		for (ApiCacheSummary summary : summaries) {
			names.append(summary.getName() + ",");
		}
		
		log.info("Caching following items: " + names.substring(0, names.length()-1).toString());
		List<ApiCacheSummary> out = new ArrayList<>();
		
		for (ApiCacheSummary summary : summaries) {
			ApiCache cache = new ApiCache(summary);
			conService.cache(cache,profileRepo.findByName(cache.getSummary().getProfileName()).get().getByName("zendesk"));
			this.cacheRepo.save(cache,summary.isDisk());
			out.add(cache.getSummary());
		}
		
		return new ResponseEntity<>(out,HttpStatus.OK);
		
	}
	
}
