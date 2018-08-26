package com.simon.apiconnect.domain.transformers;

import java.lang.reflect.InvocationTargetException;

public class TextFilter implements Filter {

	private String validString;
	private String targetField;
	
	public TextFilter() {}
	
	public TextFilter(String validString, String targetField) {
		this.validString = validString;
		this.targetField = targetField;
	}
	
	@Override
	public boolean apply(Object input) {
		String testValue = null;
		try {
			testValue = (String) getValue(input, this.targetField);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			e.printStackTrace();
		}
		if (testValue!=null && testValue instanceof java.lang.String)
			if (((java.lang.String) testValue).equalsIgnoreCase(validString))
				return true;
			else return false;
		else return false;
	}

	public String getValidString() {
		return validString;
	}

	public void setValidString(String validString) {
		this.validString = validString;
	}

	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

}
