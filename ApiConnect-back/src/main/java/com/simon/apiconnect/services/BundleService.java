package com.simon.apiconnect.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	private Map<Long,User> getUsersById() {
		Map<Long, User> requesters = null;
		requesters = this.cacheRepo.getLookupBySourceAndKey("users", "Id").getDataCasted();
		if (requesters==null) {
			log.info("Need to generate user lookup manually");
			requesters = toSimpleMap(getObjectFromCache("users", User.class, true), "Id", User.class);
		}
		return requesters;
	}
	
	private Map<Long,Org> getOrgsById() {
		Map<Long, Org> orgs = null;
		orgs = this.cacheRepo.getLookupBySourceAndKey("organisations", "Id").getDataCasted();
		if (orgs==null) {
			log.info("Need to generate Org lookup manually");
			orgs = toSimpleMap(getObjectFromCache("organisations", Org.class, true), "Id", Org.class);
		}
		return orgs;
	}
	
	public StatOrg populateOrgTickets(StatOrg org, boolean print) {

		if (this.cacheRepo.getLookups().size() ==0) {
			this.cacheRepo.getCaches();
		}
		
		// Clear the existing tickets
		org.getBundles().stream().forEachOrdered(b -> b.wipeTickets());
		
		// Get the tickets
		List<Ticket> tickets = getObjectFromCache("tickets", Ticket.class, true);

		// get the lookups from cache if available
		Map<Long,User> requesters = getUsersById();
	
		// get the Orgs
		Map<Long, Org> orgs = getOrgsById();

		try {
			org.setOrgName(orgs.get(org.getZendeskId()).getName());
			org.getBundles().stream().forEachOrdered(b -> b.setOrgName(org.getOrgName()));
		} catch (NullPointerException e) {
			log.warn("Couldn't set org name dur to null " + org.getZendeskId());
		}
		org.getBundles().stream().forEach(b -> populateTicketIds(b, tickets, requesters));
		
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
					.filter(t -> input.getLastTicketId() > 0L ? t.getId() <= input.getLastTicketId() : compareDates(input.getEndDate(),t.getCreated()))
					.map(t -> t.addUser(requesters.get(t.getRequester().getId()))).sorted()
					.forEach(t -> input.addTicket(new StatTicket(t)));
		}
		// Otherwise use created date
		else {
			tickets.stream().filter(t -> t.getOrganisation().getId() == input.getOrgZenId())
					.filter(t -> compareDates(t.getCreated(), input.getStartDate()))
					.filter(t -> input.getLastTicketId() > 0L ? t.getId() <= input.getLastTicketId() : true)
					.filter(t -> compareDates(input.getEndDate(),t.getCreated()))
					.map(t -> t.addUser(requesters.get(t.getRequester().getId()))).sorted()
					.forEach(t -> input.addTicket(new StatTicket(t)));
		}
		
		input.getTickets().stream().forEach(t -> t.updateOrgName(input.getOrgName()));

		return input;
	}
	
	public List<Object> getAllBundles(){
		List<Object> out = new ArrayList<>();
		
		for (StatOrg org : this.statOrgRepo.findAll()) {
			out.addAll(
					populateOrgTickets(org, false)
					.getBundles().stream().sorted().collect(Collectors.toList())
			);
		}
		
		return out;
	}
	
	public List<Object> getAllTickets(){
		List<Object> out = new ArrayList<>();
		for (Object o : getAllBundles()) {
			out.addAll(((StatBundle)o).getTickets().stream()
				.sorted().collect(Collectors.toList()));
		}
		return out;
	}

	public List<String> getDefaultBundleColumns() {
		List<String> toInclude = new ArrayList<>();
		toInclude.add("BundleNum");
		toInclude.add("OrgZenId");
		toInclude.add("OrgName");
		toInclude.add("StartDate");
		toInclude.add("EndDate");
		toInclude.add("BundleSize");
		toInclude.add("Balance");
		toInclude.add("Active");
		return toInclude;
	}
	
	public List<String> getDefaultTicketColumns() {
		List<String> toInclude = new ArrayList<>();
		toInclude.add("ZenOrgId");
		toInclude.add("OrgName");
		toInclude.add("BundleNum");
		toInclude.add("ZenTicketId");
		toInclude.add("CreatedDateTime");
		toInclude.add("RequesterName");
		toInclude.add("Subject");
		toInclude.add("Type");
		toInclude.add("Status");
		toInclude.add("TotalEffort");
		return toInclude;
	}
}
