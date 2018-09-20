package com.simon.apiconnect.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.apiconnect.domain.statObj.StatOrg;

public interface StatOrgRepository extends JpaRepository<StatOrg, Long> {
	
	Optional<StatOrg> findByZendeskId(long zendeskId);

}
