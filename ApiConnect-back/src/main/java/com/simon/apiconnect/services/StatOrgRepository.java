package com.simon.apiconnect.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatOrg;

public interface StatOrgRepository extends JpaRepository<StatOrg, Long> {
	
	Optional<StatOrg> findByZendeskId(long zendeskId);
	
	default void saveAndCheck(StatOrg org) {
		System.out.println("Tryng to save " + org.getZendeskId());
		try{
			this.save(org);
		} catch (Exception e) {
			System.out.println("Couldn't save " + org.getZendeskId());
			System.out.println(e.getMessage());
			for (StatBundle b : org.getBundles()) {
				System.out.println(b.getId());
			}
		}
	}

}
