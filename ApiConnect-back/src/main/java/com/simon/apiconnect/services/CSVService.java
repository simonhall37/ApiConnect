package com.simon.apiconnect.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CSVService {

	public List<String[]> readCSV(String name){
		List<String[]> out = new ArrayList<>();
		BufferedReader br = null;
		
		InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(name);
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				out.add(line.split(","));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
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
