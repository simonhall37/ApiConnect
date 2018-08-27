package com.simon.apiconnect.domain.transformers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME, 
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "type")
		@JsonSubTypes({ 
		  @Type(value = TextFilter.class, name = "text"), 
		  @Type(value = LookupFilter.class, name = "lookup") 
		})
public interface Filter {

	public boolean apply(Object input);
	
	public default Object getValue(Object input, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String methodName  = "get" + fieldName;		
		Method toInvoke = input.getClass().getDeclaredMethod(methodName, new Class[] {});
		
		return toInvoke.invoke(input, new Object[] {});
	}
	

}
