package com.simon.apiconnect;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simon.apiconnect.domain.bundle.Organisation;
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
			
			List<String[]> lines = csvService.readCSV("orgs.csv");
			int num =0;
			for (String[] l : lines) {
				if (++num>1)orgRepo.save(new Organisation(Long.parseLong(l[0]),Long.parseLong(l[1]),l[2]));
			}
			
			log.info("Started with " + orgRepo.count());
			
		};
	}
}
