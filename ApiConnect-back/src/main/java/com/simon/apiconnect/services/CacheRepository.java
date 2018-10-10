package com.simon.apiconnect.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.cache.ApiCache;
import com.simon.apiconnect.domain.cache.ApiCacheSummary;
import com.simon.apiconnect.domain.cache.ApiLookup;
import com.simon.apiconnect.domain.cache.Pair;

@Repository
public class CacheRepository {

	private final List<ApiCache> caches;
	@Value( "${cache.output.dir}")
	private String OUT_DIR;
	private static final Logger log = LoggerFactory.getLogger(CacheRepository.class);
	private final ObjectMapper om = new ObjectMapper();
	private List<ApiLookup> lookups;
	
	public CacheRepository() {
		this.lookups = new ArrayList<>();
		this.caches = new ArrayList<>();
	}

	public void writeToDisk(String cacheName) {
		
		File outFile = new File(this.OUT_DIR + "/" + cacheName + ".json");
		
		try {
			om.writerWithDefaultPrettyPrinter().writeValue(outFile, getByName(cacheName,false));
		} catch (IOException e) {
			log.error("Could not write json to disk: " + e.getMessage() + " (" + e.getCause() + ")");
		}
	}
	
	public ApiCache readFromDisk(String cacheName, boolean cacheImmediately, boolean appendExtension) {
		
		ApiCache toRead = null;
		File outFile = new File(this.OUT_DIR + "/" + cacheName + (appendExtension?".json":""));
		try {
			toRead = om.readValue(outFile, ApiCache.class);
			
			// add lookups locally
			for (Pair lookup : toRead.getSummary().getLookupSummaries()) {
				log.info("Trying to generate " + lookup.getKey() + " from key " + lookup.getValue() + " from cache " +cacheName);
				this.lookups.add(new ApiLookup(lookup.getKey(), toRead, lookup.getValue()));
			}
			
			if (cacheImmediately && toRead!=null)
				this.save(toRead, false);
		} catch (IOException e) {
			log.error("Could not read json to disk: " + e.getMessage() + " (" + e.getCause() + ")");
		}
		
		return toRead;
		
	}
	
	public void save(ApiCache cache, boolean writeToDisk) {
		ApiCache existing = null;
		if ((existing=getByName(cache.getSummary().getName(),false))!=null)
			this.caches.remove(existing);
		this.caches.add(cache);
		if (writeToDisk)
			writeToDisk(cache.getSummary().getName());
	}
	
	public ApiCache getByName(String name,boolean useDiskStorage) {
		for (ApiCache cache : this.caches) {
			if (cache.getSummary().getName().equalsIgnoreCase(name))
				return cache;
		}
		ApiCache out = null;
		if (useDiskStorage) {
			if ((out=readFromDisk(name, true,true))!=null) {
				log.info("Read cache from disk storage");
			}
		}
		
		return out;
	}
	
	public void loadAllFromDisk() {
		File outFile = new File(this.OUT_DIR);
		for (File cacheFile : outFile.listFiles()) {
			log.info("Trying to get " + cacheFile.getName() + " from disk");
			readFromDisk(cacheFile.getName(), true,false);
		}
	}
	
	public List<String> getCacheNamesOnDisk(){
		File outFile = new File(this.OUT_DIR);
		return Arrays.stream(outFile.listFiles()).map(f -> f.getName()).collect(Collectors.toList());
	}

	public boolean generateLookup(String cacheName, String lookupName, String keyName, boolean updateSummary) {
		try{
			ApiLookup lookup = new ApiLookup(lookupName,getByName(cacheName, true),keyName);
			this.lookups.add(lookup);
			if (updateSummary) {
				this.getByName(cacheName, true).getSummary().addLookup(lookupName, keyName);
				writeToDisk(cacheName);
			}
		} catch (IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	public List<ApiCache> getCaches() {
		List<String> namesOnDisk = getCacheNamesOnDisk();
		log.info("On disk: " + namesOnDisk.toString());
		if (getCacheNamesOnDisk().size() > this.caches.size()) {
			loadAllFromDisk();
		}
			
		return caches;
	}
	
	public List<ApiCacheSummary> getSummaries(){
		return getCaches().stream().map(cache -> cache.getSummary()).collect(Collectors.toList());
	}
	
	public List<ApiLookup> getLookups(){
		return this.lookups;
	}

	public ApiLookup getLookup(String name) {
		return this.lookups.stream().filter(l -> l.getName().equalsIgnoreCase(name)).findFirst().get();
	}
	
	public ApiLookup getLookupBySourceAndKey(String cacheName,String keyName) {
		return this.lookups.stream()
				.filter(l -> l.getKeyName().equalsIgnoreCase(keyName))
				.filter(l -> l.getParent().getSummary().getName().equalsIgnoreCase(cacheName))
				.findFirst().get();
	}

}
