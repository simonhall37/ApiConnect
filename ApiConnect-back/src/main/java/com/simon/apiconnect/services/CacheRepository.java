package com.simon.apiconnect.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.cache.ApiCache;
import com.simon.apiconnect.domain.cache.ApiCacheSummary;
import com.simon.apiconnect.domain.cache.ApiLookup;
import com.simon.apiconnect.domain.cache.Pair;

@Repository
public class CacheRepository {

	private final List<ApiCache> caches;
	@Value("${cache.output.dir}")
	private String OUT_DIR;
	private static final Logger log = LoggerFactory.getLogger(CacheRepository.class);
	private final ObjectMapper om = new ObjectMapper();
	private List<ApiLookup> lookups;

	public CacheRepository() {
		this.lookups = new ArrayList<>();
		this.caches = new ArrayList<>();
	}

	public List<ApiCache> getCaches() {
		if (this.caches.size() == 0)
			loadAllFromDisk();
		return this.caches;
	}

	public List<ApiCacheSummary> getSummaries() {
		return getCaches().stream().map(cache -> cache.getSummary()).collect(Collectors.toList());
	}

	public void loadAllFromDisk() {
		File outFile = new File(this.OUT_DIR);
		for (File cacheFile : outFile.listFiles()) {
			log.info("Trying to get " + cacheFile.getName() + " from disk");
			readFromDisk(cacheFile.getName());
		}
	}

	public ApiCache getByName(String name) {
		for (ApiCache cache : this.caches) {
			if (cache.getSummary().getName().equalsIgnoreCase(name)) return cache;
		}
		return null;
	}

	public ApiCache readFromDisk(String cacheName) {

		ApiCache toRead = null;
		File outFile = new File(this.OUT_DIR + "/" + cacheName);
		try {
			toRead = om.readValue(outFile, ApiCache.class);

			// add lookups locally
			for (Pair lookup : toRead.getSummary().getLookupSummaries()) {
				log.info("Trying to generate " + lookup.getKey() + " from key " + lookup.getValue() + " from cache "
						+ cacheName);
				addLookup(new ApiLookup(lookup.getKey(), toRead, lookup.getValue())); // lookup generated automatically
																						// from constructor
			}

			this.caches.add(toRead);
		} catch (IOException e) {
			log.error("Could not read json to disk: " + e.getMessage() + " (" + e.getCause() + ")");
		}

		return toRead;

	}

	public void addLookup(ApiLookup lookup) {
		
		// check the name doesn't already exist
		boolean exists = this.lookups.stream()
							.filter(l -> l.getName().equalsIgnoreCase(lookup.getName()))
							.findFirst().isPresent();
		if (exists) {
			log.warn("Not adding lookup " + lookup.getName() + " as it already exists");
			return;
		}
		// check if parent and key already exist
		exists = this.lookups.stream()
						.filter(l -> l.getKeyName().equalsIgnoreCase(lookup.getKeyName()))
						.filter(l -> l.getParent().getSummary().getName().equalsIgnoreCase(lookup.getParent().getSummary().getName()))
						.findFirst().isPresent();
		if (exists) {
			log.warn("Not adding lookup " + lookup.getName() + " as parent and key already exist in a lookup");
			return;
		}
		
		this.lookups.add(lookup);
		log.info("Added " + lookup.getName());
	}

	public ApiLookup getLookup(String name) {
		return this.lookups.stream().filter(l -> l.getName().equalsIgnoreCase(name)).findFirst().get();
	}

	public ApiLookup getLookupBySourceAndKey(String cacheName, String keyName) {
		return this.lookups.stream().filter(l -> l.getKeyName().equalsIgnoreCase(keyName))
				.filter(l -> l.getParent().getSummary().getName().equalsIgnoreCase(cacheName)).findFirst().get();
	}

	public List<ApiLookup> getLookups() {
		return this.lookups;
	}
	
	public boolean delete(ApiCacheSummary summary) {
		boolean deleted = false;
		
		// remove from cache
		deleted = this.caches.removeIf(c -> c.getSummary().getName().equalsIgnoreCase(summary.getName()));
		log.info("Deleted from cache? " + deleted);
		
		// remove associated lookups
		if (wipeLookups(summary.getName()))
			log.info("Lookups for " + summary.getName() + " removed");
		
		// remove from disk
		File outFile = new File(this.OUT_DIR);
		for (File cacheFile : outFile.listFiles()) {
			if (cacheFile.getName().equalsIgnoreCase(summary.getName() + ".json")) {
				log.info("Delete " + summary.getName() + " from disk storage");
				return cacheFile.delete();
			}
		}
		
		return false;
	}
	
	public boolean wipeLookups(String cacheName) {
		 return this.lookups.removeIf(l ->
		 	l.getParent().getSummary().getName().equalsIgnoreCase(cacheName));
	}

	public void save(ApiCache cache) {
		log.info("Saving " + cache.getSummary().getName());
		ApiCache existing = null;
		
		// remove from cache
		if ((existing = getByName(cache.getSummary().getName())) != null)
			this.caches.remove(existing);
		this.caches.add(cache);
		
		// add lookups
		wipeLookups(cache.getSummary().getName());
		for (Pair lkp : cache.getSummary().getLookupSummaries()) {
			addLookup(new ApiLookup(lkp.getKey(),cache,lkp.getValue()));
		}
		
		// persist to disk
		writeToDisk(cache.getSummary().getName());
		
	}	
	
	public void writeToDisk(String cacheName) {

		File outFile = new File(this.OUT_DIR + "/" + cacheName + ".json");
		try {
			om.writerWithDefaultPrettyPrinter().writeValue(outFile, getByName(cacheName));
		} catch (IOException e) {
			log.error("Could not write json to disk: " + e.getMessage() + " (" + e.getCause() + ")");
		}
	}

}
