package com.simon.apiconnect.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.cache.ApiCache;

@Repository
public class CacheRepository {

	private final List<ApiCache> caches;
	@Value( "${cache.output.dir}")
	private String OUT_DIR;
	private static final Logger log = LoggerFactory.getLogger(CacheRepository.class);
	private final ObjectMapper om = new ObjectMapper();
	
	public CacheRepository() {
		this.caches = new ArrayList<>();
	}

	public void writeToDisk(String cacheName) {
		
		File outFile = new File(this.OUT_DIR + cacheName + ".json");
		try {
			om.writerWithDefaultPrettyPrinter().writeValue(outFile, getByName(cacheName,false));
		} catch (IOException e) {
			log.error("Could not write json to disk: " + e.getMessage() + " (" + e.getCause() + ")");
		}
	}
	
	public ApiCache readFromDisk(String cacheName, boolean cacheImmediately) {
		
		ApiCache toRead = null;
		File outFile = new File(this.OUT_DIR + cacheName + ".json");
		try {
			toRead = om.readValue(outFile, ApiCache.class);
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
			if ((out=readFromDisk(name, true))!=null) {
				log.info("Read cache from disk storage");
			}
		}
		
		return out;
	}

	/* getters and setters */
	public List<ApiCache> getCaches() {
		return caches;
	}


}
