package com.simon.apiconnect.controllers;

import java.util.List;

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

import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.services.StatOrgRepository;

@Controller
@RequestMapping(value = "/api/organisations")
@CrossOrigin(origins = "http://localhost:4200")
public class OrgController {

	private static final Logger log = LoggerFactory.getLogger(OrgController.class);
	@Autowired
	private StatOrgRepository statOrgRepo;
	
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
	
}
