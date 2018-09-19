package com.simon.apiconnect.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.bundle.Ticket;
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatTicket;

@Service
public class BundleService {

	private static final Logger log = LoggerFactory.getLogger(BundleService.class);

	@Autowired
	private CacheRepository cacheRepo;

	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> List<T> getObjectFromCache(String name, Class T, boolean fromDisk) {

		List<T> out = new ArrayList<>();
		List<Object> initial = null;

		try {
			initial = cacheRepo.getByName(name, fromDisk).getContent();
		} catch (NullPointerException e) {
			log.error("Couldn't get the organisation content from cache or disk");
			return out;
		}
		
		for (Object obj : initial) {
			T converted = null;
			try {
				converted = (T) om.convertValue(obj, T);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to " + T.getName());
				return null;
			}
			out.add(converted);
		}
		
		return out;

	}

	private boolean compareDates(String dateTime1, String date2) {
		if (date2.compareTo(dateTime1)<=0) return true;
		else return false;
	}
	
	public StatBundle populateTicketIds(StatBundle input) {
		
		// Get the tickets
		List<Ticket> tickets = getObjectFromCache("tickets", Ticket.class, true);
		log.info(tickets.size() + " tickets retreived");
		
		// check if the firstTicketId is populated
		if (input.getFirstTicketId() > 0L) {
			tickets.stream()
				.filter(t -> t.getOrganisation().getId() == input.getOrgZenId())
				.filter(t -> t.getId() >= input.getFirstTicketId())
				.sorted()
			.forEach(t -> input.addTicket(new StatTicket(t)));
		}
		// Otherwise use created date
		else {
			tickets.stream()
			.filter(t -> t.getOrganisation().getId() == input.getOrgZenId())
			.filter(t -> compareDates(t.getCreated(),input.getStartDate()))
			.sorted()
		.forEach(t -> input.addTicket(new StatTicket(t)));
		}
		
		try {
			System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(input));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return input;
	}
}
