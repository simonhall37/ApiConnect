package com.simon.apiconnect.services;

import java.util.List;
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

@Service
public class ReportService {

	private static final Logger log = LoggerFactory.getLogger(ReportService.class);
	
	@Autowired
	private CacheRepository cacheRepo;
	
	@Autowired
	private CSVService csvWriter;
	
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	public String generateReport(long orgId, boolean addHeader) throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		List<Object> orgs = null;
		
		try {
			orgs = cacheRepo.getByName("organisations", true).getContent();
		} catch (NullPointerException e) {
			log.error("Couldn't get the organisation content from cache or disk");
			return null;
		}
		
		for (Object obj : orgs) {
			Org o = null;
			try {
				o = om.convertValue(obj, Org.class);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to Organisation");
				return null;
			}
			if (o.getId() == orgId) {
				try {
					List<String> tickets = getTickets(orgId);
					log.info("Found " + tickets.size() + " for " + o.getName());
					if (addHeader)
						sb.append(new Ticket().getHeader() + System.lineSeparator());
					for (String line : tickets) {
						sb.append(line + System.lineSeparator());
					}
				} catch (NullPointerException e) {
					log.error("Couldn't get tickets from cache or disk");
					return null;
				} catch (ClassCastException e) {
					log.error("Couldn't cast Object to Ticket");
					return null;
				}
			}
		}
		
		return sb.toString();
	}

	private List<String> getTickets(long orgId) throws NullPointerException, ClassCastException {
		return cacheRepo.getByName("tickets", true).getContent()
				.stream()
				.map(o -> om.convertValue(o, Ticket.class))
				.filter(t -> t.getOrganisationId() == orgId)
				.sorted()
				.map(t -> csvWriter.wrapContents(t.getObj()))
				.collect(Collectors.toList());
	}

	public String generateAllReports() throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		sb.append(new Ticket().getHeader() + System.lineSeparator());
		List<Object> orgs = null;
		
		try {
			orgs = cacheRepo.getByName("organisations", true).getContent();
		} catch (NullPointerException e) {
			log.error("Couldn't get the organisation content from cache or disk");
			return null;
		}
		
		for (Object obj : orgs) {
			Org o = null;
			try {
				o = om.convertValue(obj, Org.class);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to Organisation");
				return null;
			}
			String report = generateReport(o.getId(),false);
			sb.append(report);
		}
		return sb.toString();
	}

	public String generateOrgReports() {
		StringBuilder sb = new StringBuilder();
		sb.append(new Org().getHeader() + System.lineSeparator());
		List<Object> orgs = null;
		
		try {
			orgs = cacheRepo.getByName("organisations", true).getContent();
		} catch (NullPointerException e) {
			log.error("Couldn't get the organisation content from cache or disk");
			return null;
		}
		
		for (Object obj : orgs) {
			Org o = null;
			try {
				o = om.convertValue(obj, Org.class);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to Organisation");
				return null;
			}
			String report = this.csvWriter.wrapContents(o.getObj());
			sb.append(report + System.lineSeparator());
		}
		return sb.toString();
	}
}
