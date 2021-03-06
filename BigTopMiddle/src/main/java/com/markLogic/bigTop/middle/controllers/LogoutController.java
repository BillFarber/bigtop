package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marklogic.client.DatabaseClient;

@RestController
@Configuration
public class LogoutController {

	private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

	@GetMapping("/logout")
	@PostMapping("/logout")
	public void logoutPage (HttpServletRequest request, HttpServletResponse response) throws IOException {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		if (mlClient != null) {
			mlClient.release();
			logger.info("Released MarkLogic client");
		}
	    response.sendRedirect("logout");
	}
}
