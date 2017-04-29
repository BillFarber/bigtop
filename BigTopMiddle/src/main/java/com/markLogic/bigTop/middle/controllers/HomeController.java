package com.markLogic.bigTop.middle.controllers;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.markLogic.bigTop.middle.ldapDomain.Person;

@Controller
@Configuration
public class HomeController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	@GetMapping("/")
	public String index(Model model, HttpServletRequest request) throws javax.naming.NamingException, IOException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);

		model.addAttribute("person", person);
		return "home";
	}
	
	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}
