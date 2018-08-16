package com.simon.apiconnect.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.simon.apiconnect.ApiConnectApplication;
import com.simon.apiconnect.domain.ApiConnection;
import com.simon.apiconnect.domain.CredentialType;
import com.simon.apiconnect.domain.Profile;
import com.simon.apiconnect.services.ProfileRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiConnectApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ProfileControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProfileRepository profileRepo;

	@SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private final String BASE_PATH = "/api/profiles";

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {
		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
		Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

	@Test
	public void getProfile_shouldReturnProfile() throws Exception {
		when(this.profileRepo.findByName("dummy")).thenReturn(Optional.of(new Profile(1L, "dummy")));
		this.mockMvc.perform(get(BASE_PATH + "/{name}", "dummy")).andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1L)).andExpect(jsonPath("name").value("dummy"));
	}

	@Test
	public void getProfile_missingValueShouldReturnNotFound() throws Exception {
		when(this.profileRepo.findByName("dummy")).thenReturn(Optional.empty());
		this.mockMvc.perform(get(BASE_PATH + "/{name}", "dummy")).andExpect(status().isNotFound());
	}

	@Test
	public void getAllProfiles_shouldReturnList() throws Exception {

		// 2 profiles for testing
		List<Profile> expected = new ArrayList<>();
		expected.add(new Profile(1L, "dummy1"));
		Profile dummy2 = new Profile(2L, "dummy2");
		dummy2.addConnection(new ApiConnection("api1", "url", CredentialType.BASIC, "key", "value"));
		expected.add(dummy2);

		when(this.profileRepo.findAll()).thenReturn(expected);
		this.mockMvc.perform(get(BASE_PATH)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("dummy1"))).andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("dummy2"))).andExpect(jsonPath("$[1].connections").isArray())
				.andExpect(jsonPath("$[1].connections", hasSize(1)))
				.andExpect(jsonPath("$[1].connections[0].name", is("api1")))
				.andExpect(jsonPath("$[1].connections[0].baseURL", is("url")))
				.andExpect(jsonPath("$[1].connections[0].type", is("BASIC")))
				.andExpect(jsonPath("$[1].connections[0].credKey", is("key")))
				.andExpect(jsonPath("$[1].connections[0].credValue", is("value")));
	}

	@Test
	public void getAllProfiles_shouldReturnNoContent() throws Exception {
		when(this.profileRepo.findAll()).thenReturn(Lists.emptyList());
		this.mockMvc.perform(get(BASE_PATH)).andExpect(status().isNoContent());
	}

	@Test
	public void postProfile_validInputShouldSaveProfile() throws IOException, Exception {
		Profile dummy = new Profile();
		dummy.setName("dummy");
		dummy.addConnection(new ApiConnection("api1", "url", CredentialType.BASIC, "key", "value"));
		when(this.profileRepo.save(any(Profile.class))).thenReturn(dummy);

		this.mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json(dummy)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", is("http://localhost/profiles/dummy")))
				.andExpect(content().string(""))
				;
	}
	
	@Test
	public void postProfile_notJsonInputShouldReturn415() throws IOException, Exception {
		Profile dummy = new Profile();
		dummy.setName("dummy");
		when(this.profileRepo.save(any(Profile.class))).thenReturn(dummy);

		this.mockMvc.perform(post(BASE_PATH).content(json(dummy)))
				.andExpect(status().isUnsupportedMediaType())
				;
	}
	
	@Test
	public void postProfile_alreadyExistsShouldReturn409() throws IOException, Exception {
		Profile dummy = new Profile();
		dummy.setName("dummy");
		when(this.profileRepo.findByName(any(String.class))).thenReturn(Optional.of(dummy));

		this.mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json(dummy)))
				.andExpect(status().isConflict())
				;
	}
	
	@Test
	public void postProfile_emptyNameShouldReturn400() throws IOException, Exception {
		Profile dummy = new Profile();
		
		this.mockMvc.perform(post(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json(dummy)))
				.andExpect(status().isBadRequest())
				;
	}
	
	@Test
	public void postProfile_putOnPostPathShouldReturn405() throws IOException, Exception {
		Profile dummy = new Profile();
		
		this.mockMvc.perform(put(BASE_PATH).contentType(MediaType.APPLICATION_JSON).content(json(dummy)))
				.andExpect(status().isMethodNotAllowed())
				;
	}
	
	@Test
	public void deleteProfile_validDeleteShouldReturnNoContent() throws IOException, Exception {
		Profile dummy = new Profile();
		dummy.setName("dummy");
		when(this.profileRepo.findByName("dummy")).thenReturn(Optional.of(dummy));
		
		this.mockMvc.perform(delete(BASE_PATH + "/dummy"))
				.andExpect(status().isNoContent())
				.andExpect(content().string(""))
				;
	}
	
	@Test
	public void deleteProfile_deleteEmptyProfileShouldReturn404() throws IOException, Exception {
		this.mockMvc.perform(delete(BASE_PATH + "/nonexisto"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$[0].logref", is("error")))
				.andExpect(jsonPath("$[0].message", containsString("Couldn't find profile with name equal to nonexisto")))
				;
	}
	
	@Test
	public void putProfile_validProfileShouldUpdate() throws IOException, Exception {
		Profile original = new Profile(1L,"dummy");
		original.addConnection(new ApiConnection("original", "url", CredentialType.BASIC, "key", "value"));
		when(this.profileRepo.findByName("dummy")).thenReturn(Optional.of(original));
		
		Profile newProfile = new Profile(1L,"dummy");
		newProfile.addConnection(new ApiConnection("name", "url", CredentialType.BASIC, "key", "value"));
		
		this.mockMvc.perform(put(BASE_PATH + "/dummy").contentType(MediaType.APPLICATION_JSON).content(json(newProfile)))
				.andExpect(status().isOk())
				.andExpect(content().string("{\"id\":1,\"name\":\"dummy\",\"connections\":[{\"id\":0,\"name\":\"name\",\"baseURL\":\"url\",\"type\":\"BASIC\",\"credKey\":\"key\",\"credValue\":\"value\"}]}"))
				;
	}
	
	@Test
	public void putProfile_missingProfileShouldReturn404() throws IOException, Exception {
		Profile original = new Profile(1L,"dummy");
		original.addConnection(new ApiConnection("original", "url", CredentialType.BASIC, "key", "value"));
		when(this.profileRepo.findByName("dummy")).thenReturn(Optional.empty());
		
		this.mockMvc.perform(put(BASE_PATH + "/dummy").contentType(MediaType.APPLICATION_JSON).content(json(original)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$[0].logref", is("error")))
				.andExpect(jsonPath("$[0].message", containsString("Couldn't find profile with name equal to dummy")))
				;
	}

}
