package com.simon.apiconnect.domain.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.bundle.*;

public class ApiLookup {

	private String name;
	private String keyName;
	private Map<Long, Object> data;
	private String generatedOn;
	private SourceType type;
	@JsonIgnore
	private ApiCache parent;
	
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private static final Logger log = LoggerFactory.getLogger(ApiLookup.class);

	public ApiLookup(String name, ApiCache parent, String keyName) throws IllegalArgumentException {
		this.data = new HashMap<Long, Object>();
		this.name = name;
		this.type = parent.getSummary().getSource();
		this.parent = parent;
		this.keyName = keyName;
		generateMap();
	}

	public void refresh() throws IllegalArgumentException {
		generateMap();
		log.info(this.parent.getContent().size() + " items");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void generateMap() throws IllegalArgumentException {
		Class T = getClassFromType();
		this.generatedOn = LocalDateTime.now().toString();
		for (Object o : this.parent.getContent()) {
			T converted = null;
			try {
				converted = (T) om.convertValue(o, T);
			} catch (IllegalArgumentException e) {
				log.error("Can't convert from object to " + T.getName());
				throw new IllegalAccessError("Can't convert from object to " + T.getName());
			}
			try {
				Long key = (long) getField(converted, this.keyName);
				if (key!=null && key>0L) {
					this.data.put(key, converted);
				}
			} 
			catch (ClassCastException e) {
				System.out.println("key is not numeric");
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private Class getClassFromType() {
		if (this.type.equals(SourceType.ORGANIZATIONS))
			return Org.class;
		else if (this.type.equals(SourceType.USERS))
			return User.class;
		else if (this.type.equals(SourceType.TICKETS))
			return Ticket.class;
		else return null;
	}

	public Object getField(Object parent, String path) throws IllegalArgumentException {

		String[] parts = path.split("\\.");
		Object current = parent;
		for (String p : parts) {

			try {
				current = getLocalField(current, p);
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new IllegalArgumentException("Could not access " + p + " from " + current.getClass().getName()
						+ " - " + e.getMessage() + " --- " + e.getCause());
			} catch (NullPointerException | NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			}
		}

		return current;
	}

	public Object getLocalField(Object parent, String name) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		name = "get" + name;
		Method toInvoke = parent.getClass().getDeclaredMethod(name, new Class[] {});
		
		return toInvoke.invoke(parent, new Object[] {});

	}
	
	@SuppressWarnings("unchecked")
	public <T> Map<Long,T> castData() throws ClassCastException{
		Map<Long,T> out = new HashMap<>();
		for (Entry<Long,Object> pair : this.data.entrySet()) {
			try{
				out.put(pair.getKey(), (T)pair.getValue());
			} catch (ClassCastException e) {
				log.error("Could not cast " + pair.getValue().getClass());
				return null;
			}
		}
		return out;
		
	}

	/* getters and setters */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Long, Object> getData() {
		return data;
	}

	public void setData(Map<Long, Object> data) {
		this.data = data;
	}

	public String getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(String generatedOn) {
		this.generatedOn = generatedOn;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public ApiCache getParent() {
		return parent;
	}

	public void setParent(ApiCache parent) {
		this.parent = parent;
	}

	public SourceType getType() {
		return type;
	}

	public void setType(SourceType type) {
		this.type = type;
	}
}