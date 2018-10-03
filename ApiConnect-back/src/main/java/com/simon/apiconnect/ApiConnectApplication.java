package com.simon.apiconnect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.domain.statObj.StatTicket;
import com.simon.apiconnect.services.BundleService;
import com.simon.apiconnect.services.CSVService;
import com.simon.apiconnect.services.ImportService;
import com.simon.apiconnect.services.ProfileRepository;
import com.simon.apiconnect.services.StatOrgRepository;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
	@Autowired
	private BundleService bundleService;
	
	@Autowired
	private StatOrgRepository statOrgRepo;
	
	@Autowired
	private ImportService importService;
	
	@Autowired
	private ProfileRepository profileRepo;
	
	@Autowired
	private CSVService csvService;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiConnectApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			// load a default profile with permission to access Zendesk + Redmine
			setupProfile();
			
			
			importService.initialOrgImport("stat/StatOrgs.csv","stat/StatBundles.csv","stat/StatCorrections.csv","stat/StatExtra.csv",false);
			
//			
//			StatOrg cust1 = this.statOrgRepo.findByZendeskId(8359913647L).get();
//			this.bundleService.populateOrgTickets(cust1,false);
//			
//			
//			List<String> toInclude = new ArrayList<>();
//			toInclude.add("BundleNum");
//			toInclude.add("OrgZenId");
//			toInclude.add("StartDate");
//			toInclude.add("EndDate");
//			toInclude.add("BundleSize");
//			toInclude.add("Balance");
//			toInclude.add("Active");
//			
//			String out = this.csvService.toCSV(
//					cust1.getBundles().stream().sorted().collect(Collectors.toList())
//					, StatBundle.class, true, toInclude);
//			System.out.println(out);
			
			
//			StatOrg cust2 = this.statOrgRepo.findByZendeskId(34757063L).get();
//			this.bundleService.populateOrgTickets(cust2,false);
//			
//			
//			List<String> toInclude2 = new ArrayList<>();
//			toInclude2.add("CreatedDateTime");
//			toInclude2.add("RequesterName");
//			toInclude2.add("Subject");
//			toInclude2.add("Type");
//			toInclude2.add("Status");
//			toInclude2.add("TotalEffort");
//			
//			String out2 = this.csvService.toCSV(
//					cust2.getBundles().stream().sorted().collect(Collectors.toList())
//					, StatBundle.class, true, toInclude);
//			System.out.println(out2);		
//
//			String out3 = this.csvService.toCSV(
//					cust2.getBundles().stream().findFirst().get().getTickets().stream().sorted().collect(Collectors.toList())
//					, StatTicket.class, true, toInclude2);
//			System.out.println(out3);		
//			
			log.info("Started");
		};
	}
	
	private void setupProfile() {
		Profile defaultProfile = new Profile(1,"default");
		defaultProfile.addConnection(new ApiConnection("zendesk","https://wasupport.zendesk.com/api/v2/",CredentialType.BASIC,System.getenv("ZEN_USER"),System.getenv("ZEN_TOKEN")));
		defaultProfile.addConnection(new ApiConnection("redmine", "https://issues.webanywhere.co.uk/", CredentialType.TOKEN, "X-Redmine-API-Key", System.getenv("RM_API_KEY")));
		profileRepo.save(defaultProfile);
		log.info("Default profile loaded");
	}
	
	
}
