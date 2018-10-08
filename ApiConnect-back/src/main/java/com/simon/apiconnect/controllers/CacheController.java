package com.simon.apiconnect.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.simon.apiconnect.domain.Profile;
import com.simon.apiconnect.domain.cache.ApiCache;
import com.simon.apiconnect.domain.cache.ApiCacheSummary;
import com.simon.apiconnect.domain.cache.ApiLookup;
import com.simon.apiconnect.services.CacheRepository;
import com.simon.apiconnect.services.ConnectionService;
import com.simon.apiconnect.services.ProfileRepository;

@RestController
@RequestMapping(value = "/api/cache")
@CrossOrigin(origins = "http://localhost:4200")
public class CacheController {

	@Autowired
	private ConnectionService conService;
	
	@Autowired
	private CacheRepository cacheRepo;
	
	@Autowired
	private ProfileRepository profileRepo;
	
	private static final Logger log = LoggerFactory.getLogger(CacheController.class);
	
	@GetMapping(value = "/summaries")
	public ResponseEntity<List<ApiCacheSummary>> getSummaries() throws JsonParseException, JsonMappingException, IOException {
		
		try{
			List<ApiCacheSummary> summaries = this.cacheRepo.getSummaries();
			return new ResponseEntity<>(summaries, HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for summaries request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@GetMapping(value = "/summaries/{name}")
	public ResponseEntity<ApiCacheSummary> getSummary(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			ApiCacheSummary summary = this.cacheRepo.getByName(name, true).getSummary();
			return new ResponseEntity<>(summary, HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for summary request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@PutMapping(value="/summaries/{name}")
	public ResponseEntity<ApiCacheSummary> updateSummary(@PathVariable("name") String name, @Valid @RequestBody ApiCacheSummary summary) {
		ApiCache cache = this.cacheRepo.getByName(name, true);
		if (cache == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		else {
			cache.setSummary(summary);
			cache.wipeContent();
			this.cacheRepo.save(cache, true);
			return new ResponseEntity<>(cache.getSummary(), HttpStatus.OK); 
		}
	}
	
	@PostMapping(value = "/lookups/create")
	public ResponseEntity<Boolean> lookup(@RequestBody Map<String, Object> payload) {
		boolean response = false;
		if (payload.containsKey("cacheName") && payload.containsKey("lookupName") && payload.containsKey("keyName")) {
			response = this.cacheRepo.generateLookup((String)payload.get("cacheName"),(String) payload.get("lookupName"),(String) payload.get("keyName"),true);
		}
		try{;
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (NullPointerException e) {
			log.info("No data for summaries request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value = "/lookups")
	public ResponseEntity<List<ApiLookup>> getLookups() throws JsonParseException, JsonMappingException {
		try{
			return new ResponseEntity<>(this.cacheRepo.getLookups(), HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for summaries request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value = "/lookups/{name}")
	public ResponseEntity<ApiLookup> getLookup(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		try{
//			this.cacheRepo.generateLookup("organisations", "orgById", "Id");
			return new ResponseEntity<>(this.cacheRepo.getLookup(name), HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for summaries request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value = "/content/{name}")
	public ResponseEntity<List<Object>> getContent(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			List<Object> content = this.cacheRepo.getByName(name, true).getContent();
			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for content request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@PostMapping(value = "/summaries/create")
	public ResponseEntity<List<ApiCacheSummary>> cache(@RequestBody List<ApiCacheSummary> summaries) throws JsonParseException, JsonMappingException, IOException {
		
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
			
			// generate lookups
			for (String[] pair : cache.getSummary().getLookupSummaries()) {
				this.cacheRepo.generateLookup(cache.getSummary().getName(),pair[0],pair[1],false);
			}
		}
		
		return new ResponseEntity<>(out,HttpStatus.OK);
		
	}
	
}
