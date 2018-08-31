package com.simon.apiconnect.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.bundle.ExtraTicket;
import com.simon.apiconnect.domain.bundle.Org;
import com.simon.apiconnect.domain.bundle.Organisation;
import com.simon.apiconnect.domain.bundle.Ticket;
import com.simon.apiconnect.domain.bundle.TimeCorrection;
import com.simon.apiconnect.domain.bundle.User;

@Service
public class ReportService {

	private static final Logger log = LoggerFactory.getLogger(ReportService.class);
	
	@Autowired
	private CacheRepository cacheRepo;
	
	@Autowired
	private OrganisationRepository orgRepo;
	
	@Autowired
	private CSVService csvWriter;
	
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	public Map<Long,User> getUserMap(){
		Map<Long,User> out = new HashMap<>();
		
		List<Object> users = null;
		
		try {
			users = cacheRepo.getByName("users", true).getContent();
		} catch (NullPointerException e) {
			log.error("Couldn't get the organisation content from cache or disk");
			return null;
		}
		
		for (Object obj : users) {
			User o = null;
			try {
				o = om.convertValue(obj, User.class);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to Organisation");
				return null;
			}
			out.put(o.getId(), o);
		}
		
		return out;
	}
	
	public String generateReport(long orgId, boolean addHeader,Map<Long,User> users,String startDate) throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		List<Object> orgs = null;
		
		if (users==null)
			users = getUserMap();
		
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
					if (testTickets(o, startDate==null?"2000-01-01":startDate)) {
						List<String> tickets = getTickets(o,users);
						log.info("Found " + tickets.size() + " for " + o.getName());
						if (addHeader)
							sb.append(new Ticket().generateHeader() + System.lineSeparator());
						for (String line : tickets) {
							sb.append(line + System.lineSeparator());
						}
					}
					
				} catch (NullPointerException e) {
					log.error("Couldn't get tickets from cache or disk");
					e.printStackTrace();
					return null;
				} catch (ClassCastException e) {
					log.error("Couldn't cast Object to Ticket");
					return null;
				}
			}
		}
		
		return sb.toString();
	}

	private Organisation getOrganisation(long id) {
		try{
			return this.orgRepo.findByZendeskId(id).get();
		} catch (NoSuchElementException e) {
			
		}
		return null;
	}
	
	private boolean testTickets(Org orgObj,String startDate) {
		Organisation base = getOrganisation(orgObj.getId());	
		final Set<TimeCorrection> corrections = new HashSet<TimeCorrection>();
		if (base!=null) corrections.addAll(base.getCorrections());
		
		return cacheRepo.getByName("tickets", true).getContent()
				.stream()
				.map(o -> om.convertValue(o, Ticket.class))
				.filter(t -> t.getOrganisation().getId() == orgObj.getId())
				.filter(t -> base==null?true:LocalDate.parse(base.getBundleStarts()).isBefore(LocalDate.parse(t.getUpdated().substring(0, 10))))
				.anyMatch(t -> t.getEffort()>0d && LocalDate.parse(startDate).isBefore(LocalDate.parse(t.getUpdated().substring(0, 10))))
				;
	}
	
	private List<String> getTickets(Org orgObj,Map<Long,User> users) throws NullPointerException, ClassCastException {
		
		Organisation base = getOrganisation(orgObj.getId());	
		final Set<TimeCorrection> corrections = new HashSet<TimeCorrection>();
		final Set<ExtraTicket> extra = new HashSet<ExtraTicket>();
		if (base!=null) {
			extra.addAll(base.getExtra());
			corrections.addAll(base.getCorrections());
		}
		
		List<String> out = cacheRepo.getByName("tickets", true).getContent()
				.stream()
				.map(o -> om.convertValue(o, Ticket.class))
				.filter(t -> t.getOrganisation().getId() == orgObj.getId())
				.filter(t -> base==null?false:LocalDate.parse(base.getBundleStarts()).isBefore(LocalDate.parse(t.getUpdated().substring(0, 10))))
				.map(t -> t.addOrg(orgObj))
				.map(t -> t.addUser(users.get(t.getRequester().getId())))
				.sorted()
				.map(t -> csvWriter.wrapContents(t.generateContent(corrections)))
				.collect(Collectors.toList());
		
		List<String> extraTickets = extra.stream()
				.map(et -> new Ticket(et.getSubject(),et.getLastUpdated(),et.getNewEffort(),new Org(orgObj.getId(),orgObj.getName())))
				.map(t -> csvWriter.wrapContents(t.generateContent(corrections)))
				.collect(Collectors.toList());
		
		if (extraTickets.size()>0) {
			log.info("Extra tickets " + extraTickets.size() + " found for " + orgObj.getName());
			out.addAll(extraTickets);
		}
		
		return out;
	}

	public String generateAllReports(String startDate) throws JsonProcessingException {
		StringBuilder sb = new StringBuilder();
		sb.append(new Ticket().generateHeader() + System.lineSeparator());
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
			String report = generateReport(o.getId(),false,getUserMap(),startDate);
			sb.append(report);
		}
		return sb.toString();
	}

	public String generateOrgReports() {
		StringBuilder sb = new StringBuilder();
		sb.append(new Org().generateHeader() + System.lineSeparator());
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
			String report = this.csvWriter.wrapContents(o.generateContent());
			sb.append(report + System.lineSeparator());
		}
		return sb.toString();
	}
}
