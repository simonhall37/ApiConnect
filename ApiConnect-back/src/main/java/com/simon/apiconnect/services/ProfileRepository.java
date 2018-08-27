package com.simon.apiconnect.services;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simon.apiconnect.domain.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Optional<Profile> findByName(String name);
	
}
