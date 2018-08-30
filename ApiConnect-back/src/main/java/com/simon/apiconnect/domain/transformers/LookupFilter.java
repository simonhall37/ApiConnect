package com.simon.apiconnect.domain.transformers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class LookupFilter implements Filter {

	private String targetField;
	private List<String> targetValues;
	
	public LookupFilter() {}
	
	public LookupFilter(String targetField,List<String> targetValues) {
		this.targetField = targetField;
		this.targetValues = targetValues;
	}
	
	@Override
	public boolean apply(Object input) {
		String testValue = null;
		try {
			Object o = getValue(input, this.targetField);
			if (o instanceof String)
				testValue = (String)o;
			else if (o instanceof Long)
				testValue = String.valueOf(o);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			e.printStackTrace();
		}
		if (testValue!=null && testValue instanceof java.lang.String)
			if (this.targetValues.contains((java.lang.String) testValue))
				return true;
			else return false;
		else return false;
	}

	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	public List<String> getTargetValues() {
		return targetValues;
	}

	public void setTargetValues(List<String> targetValues) {
		this.targetValues = targetValues;
	}

}
