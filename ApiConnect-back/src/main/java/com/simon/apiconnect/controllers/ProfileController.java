package com.simon.apiconnect.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.simon.apiconnect.domain.Profile;
import com.simon.apiconnect.exceptions.ProfileAlreadyExistsException;
import com.simon.apiconnect.exceptions.ProfileNotFoundException;
import com.simon.apiconnect.services.ProfileRepository;

@RestController
@RequestMapping(value = "/api/profiles")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {

	private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

	private final ProfileRepository profileRepo;

	public ProfileController(ProfileRepository profileRepo) {
		this.profileRepo = profileRepo;
	}
	
	@GetMapping
	public ResponseEntity<List<Profile>> getAllProfiles() {
		List<Profile> profiles = profileRepo.findAll();

		if (profiles.size() == 0)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<List<Profile>>(profiles, HttpStatus.OK);
	}

	@GetMapping("/{name}")
	public ResponseEntity<Profile> getProfile(@PathVariable String name) {
		return profileRepo.findByName(name).map(profile -> new ResponseEntity<>(profile, HttpStatus.OK))
				.orElseThrow(() -> new ProfileNotFoundException(log, "profile", "name", name));
	}

	@PostMapping
	public ResponseEntity<?> postProfile(@Valid @RequestBody Profile profile, UriComponentsBuilder ucBuilder) {

		if (profileRepo.findByName(profile.getName()).isPresent())
			throw new ProfileAlreadyExistsException(log, profile.getName());

		Profile savedProfile = profileRepo.save(profile);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/profiles/{name}").buildAndExpand(savedProfile.getName()).toUri());
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Profile> updateProfile(@PathVariable("id") long id, @Valid @RequestBody Profile profile) {
		return profileRepo.findById(id).map(profileToUpdate -> {
			profileToUpdate.setConnections(profile.getConnections());
			profileToUpdate.setName(profile.getName());
			profileRepo.save(profileToUpdate);
			return new ResponseEntity<>(profileToUpdate, HttpStatus.OK);
		}).orElseThrow(() -> new ProfileNotFoundException(log, "profile", "id", String.valueOf(id)));
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<?> deleteProfile(@PathVariable("name") String name) {
		return profileRepo.findByName(name).map(profile -> {
			profileRepo.delete(profile);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}).orElseThrow(() -> new ProfileNotFoundException(log, "profile", "name", name));
	}
}
