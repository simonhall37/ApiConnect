package com.simon.apiconnect;

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
import com.simon.apiconnect.services.ImportService;
import com.simon.apiconnect.services.ProfileRepository;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
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
			
			// load a default profile with permission to access Zendesk + Redmine
			setupProfile();
			
			// load the metadata
			importService.initialOrgImport("stat/StatOrgs.csv","stat/StatBundles.csv","stat/StatCorrections.csv","stat/StatExtra.csv",false);
			
			log.info("Started");
		};
	}
	
	private void setupProfile() {
		Profile defaultProfile = new Profile(1,"default");
		defaultProfile.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
		defaultProfile.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));
		Profile defaultProfile2 = new Profile(2,"additional");
		defaultProfile2.addConnection(new ApiConnection("zen","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
		defaultProfile2.addConnection(new ApiConnection("red", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));profileRepo.save(defaultProfile);
		profileRepo.save(defaultProfile);
		profileRepo.save(defaultProfile2);
		log.info("Default profile loaded");
	}
	
	
}
