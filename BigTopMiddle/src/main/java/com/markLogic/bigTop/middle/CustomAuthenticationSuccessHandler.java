package com.markLogic.bigTop.middle;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicClientFactory;
import com.marklogic.client.DatabaseClient;

@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String password = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
		LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
		Person person = getCurrentUser(ldapTemplate, username);

		DatabaseClient mlClient = MarkLogicClientFactory.createMarkLogicClient(username, password);

		HttpSession session = request.getSession();
		session.setAttribute("person", person);
		session.setAttribute("mlclient", mlClient);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect("");
	}

	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
}