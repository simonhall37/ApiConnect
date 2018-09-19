package com.simon.apiconnect.services;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import com.simon.apiconnect.domain.statObj.StatOrg;

@Service
public class ImportService {

	private static final Logger log = LoggerFactory.getLogger(ImportService.class);
	@Autowired
	private OrganisationRepository repository;
	@Autowired
	private StatOrgRepository statRepo;
	@Autowired
	private CSVService csvService;

	public ImportService() {
	}

	@SuppressWarnings("rawtypes")
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
				if (C.getParameterTypes().length > 0) {
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

	private Map<Long, Set<TimeCorrection>> readCorrections() {
		Map<Long, Set<TimeCorrection>> corrections = new HashMap<>();
		List<String[]> lines = csvService.readCSV("changes.csv");
		int num = 0;
		for (String[] l : lines) {
			if (++num > 1) {
				TimeCorrection tc = new TimeCorrection(Long.parseLong(l[1]), Double.parseDouble(l[2]));
				long orgId = Long.parseLong(l[0]);
				if (corrections.containsKey(orgId))
					corrections.get(orgId).add(tc);
				else {
					HashSet<TimeCorrection> temp = new HashSet<>();
					temp.add(tc);
					corrections.put(orgId, temp);
				}
			}
		}
		return corrections;
	}

	private Map<Long, Set<ExtraTicket>> readExtra() {
		Map<Long, Set<ExtraTicket>> extra = new HashMap<>();
		List<String[]> lines = csvService.readCSV("extra.csv");
		int num = 0;
		for (String[] l : lines) {
			if (++num > 1) {
				ExtraTicket et = new ExtraTicket(l[1], l[2], Double.parseDouble(l[3]));
				long orgId = Long.parseLong(l[0]);
				if (extra.containsKey(orgId))
					extra.get(orgId).add(et);
				else {
					HashSet<ExtraTicket> temp = new HashSet<>();
					temp.add(et);
					extra.put(orgId, temp);
				}
			}
		}
		return extra;
	}

	public void importOrganisations() {
		log.info("Importing organisations");
		Map<Long, Set<ExtraTicket>> extra = readExtra();
		Map<Long, Set<TimeCorrection>> corrections = readCorrections();

		List<String[]> lines = csvService.readCSV("orgs.csv");
		int num = 0;
		for (String[] l : lines) {
			if (++num > 1) {
				Organisation o = new Organisation(Long.parseLong(l[0]), Long.parseLong(l[1]), l[2]);
				o.setCorrections(corrections.get(o.getZendeskId()));
				o.setExtra(extra.get(o.getZendeskId()));
				this.repository.save(o);
			}
		}

		log.info("Saved " + this.repository.count() + " organisations");
	}

	public <T> Map<Long, T> toMap(List<T> input, String propName, @SuppressWarnings("rawtypes") Class T) {
		Map<Long,T> out = new HashMap<>();
		for (T obj : input) {
			Object id;
			try {
				@SuppressWarnings("unchecked")
				Method m = T.getMethod("get" + propName, new Class[] {});
				id = m.invoke(obj, new Object[] {});
				out.put((Long) id, obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				log.error("Error getting key value when transforming list to map",e);
			}
		}
		return out;
	}

	public void initialOrgImport(String orgCSV, String bundleCSV, String extraCSV, String changesCSV,boolean prettyPrint) {
		
		// initial csv import
		List<StatOrg> staticOrgs = getObject(orgCSV, StatOrg.class);
		List<StatBundle> staticBundles = getObject(bundleCSV, StatBundle.class);

		// transform bundles to map with Zendesk id as key
		Map<Long, StatBundle> bundlesMap = toMap(staticBundles, "OrgZenId", StatBundle.class);
		
		// add the bundles to organisations
		staticOrgs = staticOrgs.stream().map(o -> o.addBundle(bundlesMap.get(o.getZendeskId()))).collect(Collectors.toList());
		
		
		
		//save to the repository
		staticOrgs.forEach(org -> this.statRepo.save(org));
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
