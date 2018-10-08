package com.simon.apiconnect.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.domain.statObj.StatTicket;
import com.simon.apiconnect.services.BundleService;
import com.simon.apiconnect.services.CSVService;
import com.simon.apiconnect.services.StatOrgRepository;

@Controller
@RequestMapping(value = "/api/organisations")
@CrossOrigin(origins = "http://localhost:4200")
public class OrgController {

	private static final Logger log = LoggerFactory.getLogger(OrgController.class);
	@Autowired
	private StatOrgRepository statOrgRepo;
	@Autowired
	private BundleService bundleService;
	@Autowired
	private CSVService csvService;
	
	public OrgController (StatOrgRepository statOrgRepo) {
		this.statOrgRepo = statOrgRepo;
	}
	
	@GetMapping
	public ResponseEntity<List<StatOrg>> getAllOrgs(){
		List<StatOrg> orgs = this.statOrgRepo.findAll();
		if (orgs.size() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		else {
			return new ResponseEntity<List<StatOrg>>(orgs,HttpStatus.OK);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<StatOrg> getOrg(@PathVariable long id){
		return this.statOrgRepo.findByZendeskId(id).map(org -> new ResponseEntity<>(org,HttpStatus.OK))
				.orElseThrow(() -> new NullPointerException("Organisation with id " + id + " not found"));
	}
	
	@GetMapping
	@RequestMapping(value = "/bundle/report")
	public void getBundleReport(HttpServletResponse response) {
		
		List<Object> bundles = this.bundleService.getAllBundles();
		List<String> toInclude = this.bundleService.getDefaultBundleColumns();
		
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(this.csvService.toCSV(bundles, StatBundle.class, true, toInclude));
		} catch (JsonProcessingException e) {
			log.error("JsonParse exception",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
	}
	
	@GetMapping
	@RequestMapping(value = "/ticket/report")
	public void getTicketReport(HttpServletResponse response) {
		
		long startTime = System.currentTimeMillis();
		List<Object> tickets = this.bundleService.getAllTickets();
		List<String> toInclude = this.bundleService.getDefaultTicketColumns();
		
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(this.csvService.toCSV(tickets, StatTicket.class, true, toInclude));
		} catch (JsonProcessingException e) {
			log.error("JsonParse exception",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
		log.info("Report generated in " + (System.currentTimeMillis() - startTime)/1000.0);
	}
	
}
