package com.simon.apiconnect.services;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CSVService {

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
