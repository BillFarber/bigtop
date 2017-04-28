package com.markLogic.bigTop.middle.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicClientFactory;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@Controller
@Configuration
public class SearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@GetMapping("/search")
	public String search(Model model, HttpServletRequest request) throws javax.naming.NamingException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);

		String path = request.getContextPath();
		String searchPath = path + "/search";
		String logoutPath = path + "/logout";

		model.addAttribute("person", person);
		model.addAttribute("searchPath", searchPath);
		model.addAttribute("logoutPath", logoutPath);
		return "search";
	}

	@GetMapping("/doSearch")
	public String doSearch(Model model, HttpServletRequest request, @ModelAttribute("q") String q) throws javax.naming.NamingException, IOException {
		
		logger.info("Search for: " + q);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);
		
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		if (mlClient == null) {
			mlClient = MarkLogicClientFactory.createMarkLogicClient(username, password);
			request.getSession().setAttribute("mlclient", mlClient);
		}
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris = mlService.search(q);
		logger.info("Found " + resultUris.size() + " matches");
		
		String path = request.getContextPath();
		String searchPath = path + "/search";
		String logoutPath = path + "/logout";

		model.addAttribute("person", person);
		model.addAttribute("resultUris", resultUris);
		model.addAttribute("searchPath", searchPath);
		model.addAttribute("logoutPath", logoutPath);
		return "search";
	}

	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
