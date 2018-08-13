package com.simon.apiconnect.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Profile {

	private long id;
	private String name;
	private List<ApiConnection> connections;
	
	public Profile() {
		this.connections = new ArrayList<>();
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

	public List<ApiConnection> getConnections() {
		return connections;
	}

	public void setConnections(List<ApiConnection> connections) {
		this.connections = connections;
	}
	
}
