package com.simon.apiconnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiConnectApplication {

	private static final Logger log = LoggerFactory.getLogger(ApiConnectApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ApiConnectApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			log.info("Started");
			
		};
	}
}
