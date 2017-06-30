package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@Controller
@Configuration
public class CACSearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(CACSearchController.class);

	@RequestMapping("/cacSearch")
	public String searchGet (Model model, HttpServletRequest request) throws IOException {
		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("person");
		model.addAttribute("person", person);

		List<String> resultUris = null;
		String modulation = request.getParameter("modulation");
		if (modulation != null) {
			String minimumFrequency = request.getParameter("minimumFrequency");
			String maximumFrequency = request.getParameter("maximumFrequency");
			model.addAttribute("modulation", modulation);
			model.addAttribute("minimumFrequency", minimumFrequency);
			model.addAttribute("maximumFrequency", maximumFrequency);
			session.setAttribute("modulation", modulation);
			session.setAttribute("minimumFrequency", minimumFrequency);
			session.setAttribute("maximumFrequency", maximumFrequency);

			DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
			MarkLogicService mlService = new MarkLogicService(mlClient);

			// Do search with Structured Query and Custom Transform
			resultUris = mlService.cacSearchViaTransform(modulation, minimumFrequency, maximumFrequency);
			logger.info("Structured Query with transform results count: \n" + resultUris.size() + " matches");
			model.addAttribute("resultUris", resultUris);
			
			// Do search with custom endpoint
			JsonNode endpointJsonResults = mlService.cacSearchViaEndpoint(modulation, minimumFrequency, maximumFrequency);
			ObjectMapper mapper = new ObjectMapper();
			List<String> endpointResults = new ArrayList<String>();
			logger.info("cac-search results count: \n" + endpointJsonResults.size());
			for (int i=0; i<endpointJsonResults.size(); i++) {
				String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(endpointJsonResults.get(i));
				endpointResults.add(result);
			}
			model.addAttribute("endpointResults", endpointResults);
		} else {
			model.addAttribute("modulation", session.getAttribute("modulation"));
			model.addAttribute("minimumFrequency", session.getAttribute("minimumFrequency"));
			model.addAttribute("maximumFrequency", session.getAttribute("maximumFrequency"));
		}

		return "cacSearch";
	}
}
