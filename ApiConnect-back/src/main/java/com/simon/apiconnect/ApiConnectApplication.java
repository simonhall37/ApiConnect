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

import com.simon.apiconnect.domain.bundle.Organisation;
import com.simon.apiconnect.domain.bundle.TimeCorrection;
import com.simon.apiconnect.services.CSVService;
import com.simon.apiconnect.services.OrganisationRepository;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
	@Autowired
	private OrganisationRepository orgRepo;
	
	@Autowired
	private CSVService csvService;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiConnectApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			Map<Long,Set<TimeCorrection>> corrections = new HashMap<>();
			List<String[]> lines = csvService.readCSV("changes.csv");
			int num =0;
			for (String[] l : lines) {
				if (++num>1) {
					TimeCorrection tc = new TimeCorrection(Long.parseLong(l[1]),Double.parseDouble(l[2]));
					long orgId = Long.parseLong(l[0]);
					if (corrections.containsKey(orgId))
						corrections.get(orgId).add(tc);
					else {
						HashSet<TimeCorrection> temp = new HashSet<>();
						temp.add(tc);
						corrections.put(orgId, temp);
					}
				}
			}
			
			lines = csvService.readCSV("orgs.csv");
			num =0;
			for (String[] l : lines) {
				if (++num>1) {
					Organisation o = new Organisation(Long.parseLong(l[0]),Long.parseLong(l[1]),l[2]);
					o.setCorrections(corrections.get(o.getZendeskId()));
					orgRepo.save(o);
				}
			}
			
			log.info("Started with " + orgRepo.count());
			
		};
	}
}
