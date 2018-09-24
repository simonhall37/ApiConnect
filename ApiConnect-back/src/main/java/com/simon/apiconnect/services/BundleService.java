package com.simon.apiconnect.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.bundle.Org;
import com.simon.apiconnect.domain.bundle.Ticket;
import com.simon.apiconnect.domain.bundle.User;
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.domain.statObj.StatTicket;

@Service
public class BundleService {

	private static final Logger log = LoggerFactory.getLogger(BundleService.class);

	@Autowired
	private CacheRepository cacheRepo;
	@Autowired
	private StatOrgRepository statOrgRepo;

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

	public <T> Map<Long, T> toSimpleMap(List<T> input, String propName, @SuppressWarnings("rawtypes") Class T) {
		Map<Long, T> out = new HashMap<>();
		for (T obj : input) {
			Object id;
			try {
				@SuppressWarnings("unchecked")
				Method m = T.getMethod("get" + propName, new Class[] {});
				id = m.invoke(obj, new Object[] {});
				out.put((Long) id, obj);

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log.error("Error getting key value when transforming list to map", e);
			}
		}
		return out;
	}

	private boolean compareDates(String dateTime1, String date2) {
		if (date2.compareTo(dateTime1) <= 0)
			return true;
		else
			return false;
	}

	public StatOrg populateOrgTickets(StatOrg org, boolean print) {

		// Clear the existing tickets
		org.getBundles().stream().forEach(b -> b.wipeTickets());
		
		// Get the tickets
		List<Ticket> tickets = getObjectFromCache("tickets", Ticket.class, true);

		// get the users
		Map<Long, User> requesters = toSimpleMap(getObjectFromCache("users", User.class, true), "Id", User.class);
	
		// get the Orgs
		Map<Long, Org> orgs = toSimpleMap(getObjectFromCache("organisations", Org.class, true), "Id", Org.class);

		org.getBundles().stream().forEach(b -> populateTicketIds(b, tickets, requesters));
		org.setOrgName(orgs.get(org.getZendeskId()).getName());
		org.applyCorrections();
		org.updateOrgDetails();
		this.statOrgRepo.save(org);

		if (print) {
			try {
				System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(org));
			} catch (JsonProcessingException e) {
				log.error("Error printing results to console",e);
			}
		}
		

		return org;

	}

	public StatBundle populateTicketIds(StatBundle input, List<Ticket> tickets, Map<Long, User> requesters) {

		// check if the firstTicketId is populated
		if (input.getFirstTicketId() > 0L) {
			tickets.stream().filter(t -> t.getOrganisation().getId() == input.getOrgZenId())
					.filter(t -> t.getId() >= input.getFirstTicketId())
					.filter(t -> input.getLastTicketId() > 0L ? t.getId() <= input.getLastTicketId() : true)
					.map(t -> t.addUser(requesters.get(t.getRequester().getId()))).sorted()
					.forEach(t -> input.addTicket(new StatTicket(t)));
		}
		// Otherwise use created date
		else {
			tickets.stream().filter(t -> t.getOrganisation().getId() == input.getOrgZenId())
					.filter(t -> compareDates(t.getCreated(), input.getStartDate()))
					.filter(t -> input.getLastTicketId() > 0L ? t.getId() <= input.getLastTicketId() : true)
					.map(t -> t.addUser(requesters.get(t.getRequester().getId()))).sorted()
					.forEach(t -> input.addTicket(new StatTicket(t)));
		}

		return input;
	}
}
