package com.simon.apiconnect.controllers;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for summaries request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@GetMapping(value = "/summaries/{name}")
	public ResponseEntity<ApiCacheSummary> getSummary(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			ApiCacheSummary summary = this.cacheRepo.getByName(name).getSummary();
			return new ResponseEntity<>(summary, HttpStatus.OK);
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for summary request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@GetMapping(value = "/lookups")
	public ResponseEntity<List<ApiLookup>> getlookups() {
		try{
			return new ResponseEntity<>(cacheRepo.getLookups(), HttpStatus.OK);
		} catch (NullPointerException e) {
			log.info("No data for lookups request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value = "/content/{name}")
	public ResponseEntity<List<Object>> getContent(@PathVariable String name) throws JsonParseException, JsonMappingException, IOException {
		
		try{
			List<Object> content = this.cacheRepo.getByName(name).getContent();
			return new ResponseEntity<>(content, HttpStatus.OK);
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for content request : " + name);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
	}
	
	@DeleteMapping("/summaries/{name}")
	public ResponseEntity<?> deleteSummary(@PathVariable("name") String name) {
		ApiCache toDelete = null;
		try{
			toDelete = this.cacheRepo.getByName(name);
		} catch (NoSuchElementException e) {}
		if (toDelete!=null) {
			boolean deleted = cacheRepo.delete(toDelete.getSummary());
			if (deleted)
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PutMapping(value="/summaries/{name}")
	public ResponseEntity<ApiCacheSummary> updateSummary(@PathVariable("name") String name, @Valid @RequestBody ApiCacheSummary summary) {
		ApiCache cache = null;
		try{
			cache = this.cacheRepo.getByName(name);
		} catch (NoSuchElementException e) {
			log.warn("No Such Element durig PUT");
		}
		if (cache == null) {
			log.info("No content for " + name);
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		else {
			cache.setSummary(summary);
			cache.wipeContent();
			this.cacheRepo.save(cache);
			return new ResponseEntity<>(cache.getSummary(), HttpStatus.OK); 
		}
	}
	
	@PostMapping(value = "/summaries")
	public ResponseEntity<ApiCacheSummary> cache(@RequestBody ApiCacheSummary summary) throws JsonParseException, JsonMappingException, IOException {
		
		ApiCache cache = null;
		try{
			cache = this.cacheRepo.getByName(summary.getName());
		} catch (NoSuchElementException e) {}
		if (cache == null) {
			cache = new ApiCache(summary);
			this.cacheRepo.save(cache);
			return new ResponseEntity<>(cache.getSummary(),HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null,HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping(value="/summaries/{name}")
	public ResponseEntity<ApiCacheSummary> cache(@PathVariable String name) {
		ApiCache cache = null;
		try{
			cache = this.cacheRepo.getByName(name);
		} catch (NoSuchElementException e) {}
		if (cache!=null)
			return new ResponseEntity<>(executeCache(cache.getSummary()),HttpStatus.OK);
		else return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
	}
	
	
	private ApiCacheSummary executeCache(ApiCacheSummary summary) {
		ApiCache cache = new ApiCache(summary);
		conService.cache(cache,profileRepo.findByName(cache.getSummary().getProfileName()).get().getByName("zendesk"));

		this.cacheRepo.save(cache);

		return cache.getSummary();
	}
	
}
