package com.simon.apiconnect.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CSVService {

	private static final Logger log = LoggerFactory.getLogger(CSVService.class);
	
	public List<String[]> readCSV(String name){
		List<String[]> out = new ArrayList<>();
		BufferedReader br = null;
		
		InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(name);
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] splitLine = line.split(",");
				for (int i=0;i<splitLine.length;i++) {
					if (splitLine[i].startsWith("\"") && splitLine[i].endsWith("\"")) {
						splitLine[i] = splitLine[i].substring(1, splitLine[i].length()-1);
					}
				}
				out.add(splitLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public String toCSV(List<Object> input, @SuppressWarnings("rawtypes") Class T, boolean includeHeader, List<String> toInclude) {
		StringBuilder sb = new StringBuilder();
		
		if (includeHeader) {
			for (String h : toInclude) {
				sb.append(h + ",");
			}
			try{
				sb.replace(sb.length()-1, sb.length(), "\n");
			} catch(StringIndexOutOfBoundsException e) {
				log.error(sb.toString() + " --- " + sb.length(),e);
			}
		}
		
		List<Method> methods = new ArrayList<>();
		
		for (String field : toInclude) {
			try {
				methods.add(T.getMethod("get"+field, new Class[] {}));
			} catch (SecurityException | NoSuchMethodException e) {
				log.error("Can't find field " + field + " in object "+ T.getSimpleName().toString());
				return null;
			}
		}
		
		for (Object o : input) {
			for (Method m : methods) {
				try {
					sb.append(m.invoke(o, new Object[] {}) + ",");
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					log.error("Can't access method " + m.getName(),e);
					sb.append(",");
				}
			}
			try{
				sb.replace(sb.length()-1, sb.length(), "\n");
			} catch(StringIndexOutOfBoundsException e) {
				log.error(sb.toString() + " --- " + sb.length(),e);
			}		
		}
		
		return sb.toString();
	}
	
	public String wrapContents(List<Object> input) {
		StringBuilder sb = new StringBuilder();
		for (Object o : input) {
			if (o instanceof String)
				sb.append("\"" + ((String)o).replaceAll("\"", "\"\"").replaceAll("\n", "") + "\"");
			else if (o instanceof Integer)
				sb.append(o);
			else if (o instanceof Double)
				sb.append(o);
			else if (o instanceof Long)
				sb.append(o);
			sb.append(",");
		}
		
		return sb.substring(0, sb.length()-1).toString();
	}
	
}
