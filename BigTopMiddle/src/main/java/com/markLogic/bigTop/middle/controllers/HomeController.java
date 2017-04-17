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
import org.springframework.web.bind.annotation.RestController;

import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicClient;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@RestController
@Configuration
public class HomeController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/")
	public String index() throws javax.naming.NamingException, IOException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		logger.info("username: " + username);
		logger.info("password: " + password);

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);

		DatabaseClient mlClient = MarkLogicClient.getMarkLogicClient(username, password);
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris = mlService.search("");
		StringBuilder resultDiv = new StringBuilder("<div>Search Results<ol>");
		for (String uri : resultUris) {
			resultDiv.append("<li>"+uri+"</li>");
		}
		resultDiv.append("</ol></div>");
		String head = "<head><title>BigTop Middle</title></head>";
		String welcome = "<h1>Welcome to the home page " + person.getFullName() + "!</h1>";
		String searchLink = "<div><a href='/search'>Search Page</a></div>";
		String logoutLink = "<div><a href='/logout'>Logout</a></div>";
		String body = "<body>"+welcome+person.toHtmlDiv()+searchLink+logoutLink+"</body>";

		return "<html>"+head+body+"</html>";
	}
	
	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
