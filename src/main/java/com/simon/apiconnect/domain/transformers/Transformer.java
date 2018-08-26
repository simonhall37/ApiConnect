package com.simon.apiconnect.domain.transformers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simon.apiconnect.domain.cache.SourceType;

public abstract class Transformer {

	private final SourceType type;
	private Filter filter;
	
	public Transformer(SourceType type,Filter filter) {
		this.type = type;
		this.filter = filter;
	}
	
	public abstract List<Object> transform(Page page) throws JsonParseException, JsonMappingException, IOException;

	protected String trimZone(String input) {
		if (input.endsWith("Z"))
			return input.substring(0, input.lastIndexOf("Z"));
		else return input;
	}
	
	protected Object setField(JsonNode node,boolean numeric, boolean isDecimal) {
		try {
			if (numeric) 
				if (isDecimal)
					return node.asDouble();
				else
					return node.asLong();
			else return node.asText();
		} catch (NullPointerException e) {
			
		}
		return null;
	}

	
	public SourceType getType() {
		return type;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
}
