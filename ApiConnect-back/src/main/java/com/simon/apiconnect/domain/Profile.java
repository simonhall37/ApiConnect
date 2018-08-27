package com.simon.apiconnect.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;


@Entity
@Table(name="profile")
public class Profile {

	@Id
	@GeneratedValue
	private long id;
	
	@NotBlank
	private String name;
	
	@OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
	private Set<ApiConnection> connections = new HashSet<>();
	
	public Profile() {
	}

	public Profile(long id, String name) {
		this();
		this.id = id;
		this.name = name;
	}
	
	public boolean addConnection(ApiConnection conn) {
		return this.connections.add(conn);
	}
	
	public ApiConnection getByName(String name) {
		for (ApiConnection conn : this.connections) {
			if (conn.getName().equalsIgnoreCase(name)) 
				return conn;
		}
		return null;
	}
	
	/* getters and setters */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ApiConnection> getConnections() {
		return connections;
	}

	public void setConnections(Set<ApiConnection> connections) {
		this.connections = connections;
	}
	
}
