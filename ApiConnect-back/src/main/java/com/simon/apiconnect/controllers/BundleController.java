package com.simon.apiconnect.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.cache.ApiCacheSummary;
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.services.BundleService;

@Controller
@RequestMapping(value = "/api/orgs")
@CrossOrigin(origins = "http://localhost:4200")
public class BundleController {

	private static final Logger log = LoggerFactory.getLogger(BundleController.class);
	@Autowired
	private BundleService bundleService;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
	@GetMapping
	public ResponseEntity<List<StatOrg>> getBundles(@RequestParam(value="recalc",required = false) boolean recalc){

		try{
			List<StatOrg> bundles = this.bundleService.getBundlesWithNames(recalc);
			return new ResponseEntity<>(bundles, HttpStatus.OK);
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for bundles request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value="/{orgId}")
	public ResponseEntity<StatOrg> getBundle(@PathVariable long orgId,@RequestParam(value="recalc",required = false) boolean recalc){

		try{
			StatOrg bundle = this.bundleService.getBundleWithName(orgId,recalc);
			return new ResponseEntity<>(bundle, HttpStatus.OK);
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for bundle request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value="/{orgId}/tickets")
	public ResponseEntity<StatOrg> getBundleWithTickets(@PathVariable long orgId,@RequestParam(value="recalc",required = false) boolean recalc){
		try{
			StatOrg bundle = this.bundleService.getBundleWithName(orgId,recalc);
			if (recalc) {
				bundle = this.bundleService.populateOrgTickets(bundle, false);
				log.info("Re-calculating tickets for " + bundle.getOrgName());
			}
			return new ResponseEntity<>(bundle, HttpStatus.OK);
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for bundle request");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@PutMapping(value="/{orgId}")
	public ResponseEntity<StatOrg> putOrg(@PathVariable("orgId") long orgId, @Valid @RequestBody StatOrg org){
		
//		try {
//			System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(org));
//		} catch (JsonProcessingException e1) {
//			e1.printStackTrace();
//		}
		try{
			StatOrg toReturn = this.bundleService.checkAndUpdate(org);
			if (toReturn!=null) {
				return new ResponseEntity<>(toReturn, HttpStatus.OK);
			}
		} catch (NullPointerException | NoSuchElementException e) {
			log.info("No data for bundle request");
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	
	
	
}
