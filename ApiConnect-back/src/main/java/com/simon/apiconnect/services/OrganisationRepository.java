package com.simon.apiconnect.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.apiconnect.domain.bundle.Organisation;

public interface OrganisationRepository extends JpaRepository<Organisation,Long> {

	Optional<Organisation> findByZendeskId(long zendeskId);
}
