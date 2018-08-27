package com.simon.apiconnect.domain.transformers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simon.apiconnect.domain.bundle.User;
import com.simon.apiconnect.domain.cache.SourceType;

public class TransformerUser extends Transformer {

	private int LIMIT = 10000;
	
	public TransformerUser(Filter filter) {
		super(SourceType.USERS, filter);
	}

	@Override
	public List<Object> transform(Page page) throws JsonParseException, JsonMappingException, IOException {
		List<Object> out = new ArrayList<>();

		JsonNode entries = page.getContent().get(page.getContentWrapper());

		if (entries.isArray()) {
			for (JsonNode child : entries) {
				
				User temp = new User();
				temp.setId((long) setField(child.get("id"), true, false));
				temp.setName((String) setField(child.get("name"), false, false));
				
				if (this.getFilter()!=null && this.getFilter().apply(temp))
					out.add(temp);
				else if (this.getFilter() == null) out.add(temp);
			}
		}
		
		if (page.getNextPage() !=null && page.getNextPage().contains(String.valueOf(this.LIMIT)))
			page.setNextPage("null");
		
		return out;
	}

	public int getLIMIT() {
		return LIMIT;
	}

	public void setLIMIT(int lIMIT) {
		LIMIT = lIMIT;
	}

}
