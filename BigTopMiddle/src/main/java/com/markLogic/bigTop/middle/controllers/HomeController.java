package com.markLogic.bigTop.middle.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

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

import com.markLogic.bigTop.middle.PullJsonFromMarkLogic;
import com.markLogic.bigTop.middle.ldapDomain.Person;

@RestController
@Configuration
public class HomeController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@GetMapping("/")
	public String index() throws javax.naming.NamingException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		logger.info("username: " + username);
		logger.info("password: " + password);

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);

		PullJsonFromMarkLogic obj = new PullJsonFromMarkLogic(username, password);
		List<String> resultUris = obj.search("");
		StringBuilder resultDiv = new StringBuilder("<div>Search Results<ol>");
		for (String uri : resultUris) {
			resultDiv.append("<li>"+uri+"</li>");
		}
		resultDiv.append("</ol></div>");
		String head = "<head><title>BigTop Middle</title></head>";
		String welcome = "<h1>Welcome to the home page " + person.getFullName() + "!</h1>";
		String logoutLink = "<div><a href='/logout'>Logout</a></div>";
		String body = "<body>"+welcome+person.toHtmlDiv()+logoutLink+"</body>";

		return "<html>"+head+body+"</html>";
	}
	
	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
