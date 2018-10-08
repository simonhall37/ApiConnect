package com.simon.apiconnect.domain.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.simon.apiconnect.domain.transformers.Filter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiCacheSummary {

	private String name;
	private String path;
	private String profileName;
	private SourceType source;
	private int size;
	private String updatedOn;
	private Filter filter;
	private Map<String,String> params;
	private List<String[]> lookupSummaries;
	private boolean disk;

	public ApiCacheSummary() {	
		this.setParams(new HashMap<>());
		this.lookupSummaries = new ArrayList<>();
	}
	
	public ApiCacheSummary(String name, String path, String profileName, SourceType source, Filter filter) {
		this();
		this.name = name;
		this.path = path;
		this.profileName = profileName;
		this.source = source;
		this.filter = filter;
	}
	
	public void addParam(String key, String value) {
		this.params.put(key, value);
	}
	
	public String getParamString() {
		if (this.params.size()==0) return "?";
		StringBuilder sb = new StringBuilder("?");
		for (Entry<String,String> e : this.params.entrySet()) {
			sb.append(e.getKey() + "=" + e.getValue() + "&");
		}
		return sb.subSequence(0, sb.length()-1).toString();
	}
	
	public void addLookup(String lookupName, String keyName) {
		this.lookupSummaries.add(new String[] {lookupName,keyName});
	}
	
	public boolean removeLookup(String lookupName) {
		return this.lookupSummaries.removeIf(pair -> pair[0].equalsIgnoreCase(lookupName));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public SourceType getSource() {
		return source;
	}

	public void setSource(SourceType source) {
		this.source = source;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Map<String,String> getParams() {
		return params;
	}

	public void setParams(Map<String,String> params) {
		this.params = params;
	}

	public boolean isDisk() {
		return disk;
	}

	public void setDisk(boolean disk) {
		this.disk = disk;
	}

	public List<String[]> getLookupSummaries() {
		return lookupSummaries;
	}

	public void setLookupSummaries(List<String[]> lookupSummaries) {
		this.lookupSummaries = lookupSummaries;
	}
	
	
	
}
