package com.simon.apiconnect.domain.transformers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.simon.apiconnect.domain.bundle.Ticket;
import com.simon.apiconnect.domain.cache.SourceType;

public class TransformerTicket extends Transformer {

	private final long DEFAULT_SIZE = 1000;

	public TransformerTicket(Filter filter) {
		super(SourceType.TICKETS,filter);
	}

	public List<Object> transform(Page page) throws JsonParseException, JsonMappingException, IOException {
		List<Object> out = new ArrayList<>();

		JsonNode entries = page.getContent().get(page.getContentWrapper());

		if (entries.isArray()) {
			for (JsonNode child : entries) {
				
				Ticket temp = new Ticket();
				temp.setId((long) setField(child.get("id"), true, false));
				temp.setSubject((String) setField(child.get("subject"), false, false));
				temp.setOrganisationId((long) setField(child.get("organization_id"), true, false));
				temp.setRequesterId((long) setField(child.get("requester_id"), true, false));
				temp.setPriority((String) setField(child.get("priority"), false, false));
				temp.setType((String) setField(child.get("type"), false, false));
				temp.setStatus((String) setField(child.get("status"), false, false));
				temp.setCreated(trimZone((String) setField(child.get("created_at"), false, false)));
				temp.setUpdated(trimZone((String) setField(child.get("updated_at"), false, false)));
				
				JsonNode fields = child.get("fields");
				for (JsonNode grandChild : fields) {
					if (grandChild.get("id").asLong() == 23800557)
						temp.setDivision((String) setField(grandChild.get("value"), false,false));
					else if (grandChild.get("id").asLong() == 33835867)
						temp.setEffort((double) setField(grandChild.get("value"), true, true));
				}
				
				if (this.getFilter()!=null && this.getFilter().apply(temp))
					out.add(temp);
				else if (this.getFilter() == null) out.add(temp);
			}
		}
		
		if (page.getContent().get("count").asLong() < DEFAULT_SIZE)
			page.setNextPage("null");
		
		return out;
	}
	
}
