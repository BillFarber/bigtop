package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;
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

import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@Controller
@Configuration
public class SearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@RequestMapping("/search")
	public String searchPost(Model model, HttpServletRequest request)
			throws javax.naming.NamingException, IOException {

		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("person");

		List<String> resultUris = null;
		String q = request.getParameter("q");
		if (q != null) {
			logger.info("Search for: " + q);
			DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
			MarkLogicService mlService = new MarkLogicService(mlClient);
			resultUris = mlService.search(q);
			logger.info("Found " + resultUris.size() + " matches");
		}

		model.addAttribute("person", person);
		model.addAttribute("resultUris", resultUris);
		return "search";
	}
}
