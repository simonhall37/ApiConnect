package com.simon.apiconnect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;
import com.simon.apiconnect.domain.Profile;
import com.simon.apiconnect.domain.bundle.ExtraTicket;
import com.simon.apiconnect.domain.bundle.Organisation;
import com.simon.apiconnect.domain.bundle.TimeCorrection;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.services.CSVService;
import com.simon.apiconnect.services.ImportService;
import com.simon.apiconnect.services.OrganisationRepository;
import com.simon.apiconnect.services.ProfileRepository;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
	@Autowired
	private OrganisationRepository orgRepo;
	
	@Autowired
	private ImportService importService;
	
	@Autowired
	private ProfileRepository profileRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiConnectApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
//			importService.importOrganisations();
			
			List<StatOrg> staticOrgs = importService.getObject("stat/StatOrgs.csv", StatOrg.class);
			log.info("Read in " + staticOrgs.size());
			
			Profile defaultProfile = new Profile(1,"default");
			defaultProfile.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
			defaultProfile.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));

			profileRepo.save(defaultProfile);
			log.info("Default profile loaded");
			
			log.info("Started");
		};
	}
	
	
}
