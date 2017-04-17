package com.markLogic.bigTop.middle.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicClient;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@RestController
@Configuration
public class SearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@GetMapping("/search")
	public String search() throws javax.naming.NamingException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);
		logger.info("person: " + person);

		String welcome = "<h1>Welcome to the search page " + person.getFullName() + "!</h1>";
		String logoutLink = "<div><a href='/logout'>Logout</a></div>";
		String queryForm = "<div><form method='GET' action='/doSearch'><input type='text' name='q' value=''><input type='submit' value='Submit'></form></div>";
		return "<html><head><title>BigTop Middle</title></head><body>"+welcome.toString()+person.toHtmlDiv()+queryForm.toString()+logoutLink+"</body></html>";
	}

	@GetMapping("/doSearch")
	public String doSearch(@ModelAttribute("q") String q) throws javax.naming.NamingException, IOException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		if (q == null) {
			System.out.println("q is null");
		} else {
			System.out.println("q: " + q);
		}
		logger.info("Search for: " + q);

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);
		logger.info("person: " + person);
		
		DatabaseClient mlClient = MarkLogicClient.getMarkLogicClient(username, password);
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris = mlService.search(q);
		StringBuilder resultDiv = new StringBuilder("<div><h3>Search Results</h3><ol>");
		for (String uri : resultUris) {
			resultDiv.append("<li>"+uri+"</li>");
		}
		resultDiv.append("</ol></div>");

		String welcome = "<h1>Welcome to the search page " + person.getFullName() + "!</h1>";
		String logoutLink = "<div><a href='/logout'>Logout</a></div>";
		String queryForm = "<div><form method='GET'><input type='text' name='q' value='"+q+"'><input type='submit' value='Submit'></form></div>";
		return "<html><head><title>BigTop Middle</title></head><body>"+welcome.toString()+person.toHtmlDiv()+queryForm+resultDiv.toString()+logoutLink+"</body></html>";
	}

	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
