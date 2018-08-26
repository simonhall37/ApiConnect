package com.simon.apiconnect.domain.transformers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simon.apiconnect.domain.bundle.Org;
import com.simon.apiconnect.domain.cache.SourceType;

public class TransformerOrg extends Transformer {
	
	public TransformerOrg(Filter filter) {
		super(SourceType.ORGANIZATIONS,filter);
	}
	
	public List<Object> transform(Page page) throws JsonParseException, JsonMappingException, IOException {
		List<Object> out = new ArrayList<>();
		
		JsonNode entries = page.getContent().get(page.getContentWrapper());
		
		if (entries.isArray()) {
			for (JsonNode child : entries) {
				
				Org temp = new Org();
				temp.setId(child.get("id").longValue());
				temp.setName(child.get("name").asText());
				temp.setType(child.get("organization_fields").get("division").asText());
				
				if (getFilter()!=null && getFilter().apply(temp))
					out.add(temp);
				else if (getFilter() == null) out.add(temp);
				
			}
		}
		
		return out;
	}

}
