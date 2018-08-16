package com.simon.apiconnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;
import com.simon.apiconnect.domain.Profile;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
//	@Autowired
//	private ConnectionService conService;
//	
//	@Autowired
//	private ProfileRepository profileRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiConnectApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			Profile profile1 = new Profile(1,"profile1");
			profile1.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
			profile1.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));
			
			Profile profile2 = new Profile(2,"profile2");
			profile2.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
			profile2.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));
			
//			profileRepo.save(profile1);
//			profileRepo.save(profile2);
			
//			final String out = conService.Connect(profile1.getByName("zendesk"), "tickets.json");
//			final String out2 = conService.Connect(profile1.getByName("redmine"), "users.json");
			
//			log.info(out.substring(0, 200));
//			log.info(out2.substring(0, 200));
			
			log.info("Started");
			
		};
	}
}
