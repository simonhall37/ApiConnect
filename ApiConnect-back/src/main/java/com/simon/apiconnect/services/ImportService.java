package com.simon.apiconnect.services;

import static org.assertj.core.api.Assertions.filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.apiconnect.domain.bundle.ExtraTicket;
import com.simon.apiconnect.domain.bundle.Organisation;
import com.simon.apiconnect.domain.bundle.TimeCorrection;
import com.simon.apiconnect.domain.statObj.StatBundle;
import com.simon.apiconnect.domain.statObj.StatCorrection;
import com.simon.apiconnect.domain.statObj.StatOrg;
import com.simon.apiconnect.domain.statObj.StatTicket;

@Service
public class ImportService {

	private static final Logger log = LoggerFactory.getLogger(ImportService.class);
	@Autowired
	private StatOrgRepository statRepo;
	@Autowired
	private CSVService csvService;

	public ImportService() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> List<T> getObject(String csvPath, Class T) {

		List<T> out = new ArrayList<>();
		List<String[]> lines = this.csvService.readCSV(csvPath);
		int lineNum = 0;

		for (String[] line : lines) {
			Object[] input = new Object[line.length];
			int index = 0;
			for (String field : line) {
				input[index] = field != null ? field : ".";
				index++;
			}
			for (Constructor C : T.getConstructors()) {
				if (C.getParameterTypes().length == input.length) {
					if (lineNum++ > 0) {
						try {
							out.add((T) C.newInstance(input));
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
							log.error("Error creating object (" + T.getName() + ")", e);
						} catch (IllegalArgumentException e2) {
							System.out.println(Arrays.toString(input));
							for (Object o : input) {
								log.warn(o.toString());
							}
							log.error("Error creating object (" + T.getName() + ")", e2);
						}
					}
				}
			}
		}

		return out;
	}

	public <T> Map<Long, List<T>> toMap(List<T> input, String propName, @SuppressWarnings("rawtypes") Class T) {
		Map<Long,List<T>> out = new HashMap<>();
		for (T obj : input) {
			Object id;
			try {
				@SuppressWarnings("unchecked")
				Method m = T.getMethod("get" + propName, new Class[] {});
				id = m.invoke(obj, new Object[] {});
				if (!out.containsKey((Long)id)) {
					List<T> initialList = new ArrayList<>();
					initialList.add(obj);
					out.put((Long) id, initialList);
				}
				else {
					List<T> initialList = out.get((Long)id);
					initialList.add(obj);
					out.put((Long)id, initialList);
				}
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log.error("Error getting key value when transforming list to map",e);
			}
		}
		return out;
	}

	public void initialOrgImport(String orgCSV, String bundleCSV,String changesCSV, String extraCSV,boolean prettyPrint) {
		
		// initial csv import
		List<StatOrg> staticOrgs = getObject(orgCSV, StatOrg.class);
		List<StatBundle> staticBundles = getObject(bundleCSV, StatBundle.class);
		List<StatCorrection> staticCorrections = getObject(changesCSV, StatCorrection.class);
		List<StatTicket> staticExtra = getObject(extraCSV,StatTicket.class);

		// transform bundles to map with Zendesk id as key
		Map<Long, List<StatBundle>> bundlesMap = toMap(staticBundles, "OrgZenId", StatBundle.class);
		Map<Long, List<StatCorrection>> correctionsMap = toMap(staticCorrections, "ZenOrgId", StatCorrection.class);
		Map<Long,List<StatTicket>> extraMap = toMap(staticExtra,"ZenOrgId",StatTicket.class);
		
		// add the bundles to organisations
		staticOrgs = staticOrgs.stream()
						.map(o -> o.addCorrections(correctionsMap.get(o.getZendeskId())))
						.map(o -> o.addBundles(bundlesMap.get(o.getZendeskId())))
						.map(o -> o.addExtraTickets(extraMap.get(o.getZendeskId())))
						.collect(Collectors.toList());
		
		//save to the repository
		ObjectMapper om = new ObjectMapper();
		staticOrgs.stream()
			.forEach(org -> 
				this.statRepo.save(org));
		log.info("Saved " + this.statRepo.count() + " orgs to db");
		
		if (prettyPrint) {
			printResults();
		}
	}
	
	@Transactional(value=TxType.MANDATORY)
	private void printResults() {
		ObjectMapper om = new ObjectMapper();
		for (StatOrg org : statRepo.findAll()) {
			try {
				System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(org));
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
			}
		}
	}

}
