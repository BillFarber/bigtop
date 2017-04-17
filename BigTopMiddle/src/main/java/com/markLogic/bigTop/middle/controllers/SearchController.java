package com.markLogic.bigTop.middle.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

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

@RestController
@Configuration
public class SearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@GetMapping("/search")
	public String searchGet(@ModelAttribute("q") String q) throws javax.naming.NamingException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		logger.info("Search for: " + q);

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);
		logger.info("person: " + person);
		String welcome = "<h1>Welcome to the search page " + person.getFullName() + "!</h1>";
		String logoutLink = "<div><a href='/logout'>Logout</a></div>";
		String queryForm = "<div><form method='GET'><input type='text' name='q'><input type='submit' value='Submit'></form></div>";
		return "<html><head><title>BigTop Middle</title></head><body>"+welcome.toString()+person.toHtmlDiv()+queryForm+logoutLink+"</body></html>";
	}

	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
