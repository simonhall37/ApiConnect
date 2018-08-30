package com.simon.apiconnect.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simon.apiconnect.services.ReportService;

@Controller
@RequestMapping(value = "/api/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	private static final Logger log = LoggerFactory.getLogger(ReportService.class);
	
	@GetMapping
	@RequestMapping(value = "/tickets/{id}")
	public void getTicketReport(@PathVariable Long id,HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(reportService.generateReport(id,true,null));
		} catch (JsonProcessingException e) {
			log.error("JsonParse exception",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
	}
	
	@GetMapping
	@RequestMapping(value = "/tickets")
	public void getTicketReports(HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(reportService.generateAllReports());
		} catch (JsonProcessingException e) {
			log.error("JsonParse exception",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
	}
	
	@GetMapping
	@RequestMapping(value = "/organisations")
	public void getOrgReports(HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		try {
			response.getWriter().print(reportService.generateOrgReports());
		} catch (JsonProcessingException e) {
			log.error("JsonParse exception",e);
		} catch (IOException e) {
			log.error("IOException",e);
		}
	}
	
}
