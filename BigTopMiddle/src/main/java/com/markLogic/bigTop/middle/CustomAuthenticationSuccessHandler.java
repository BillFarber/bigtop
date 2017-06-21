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
		
		// For demo purposes, give the user a default DoubleEllipse.
		// Massachusetts, Connecticut, Rhode Island, Vermont, New Hampshire, and Maine
		Ellipse ellipseA = new Ellipse("42.25", "-71.3", "0.9", "1.8", "0");
		// Massachusetts, Connecticut, and New York
		Ellipse ellipseB = new Ellipse("41.5", "-73.2", "0.6", "1.2", "0");
		DoubleEllipse doubleEllipse = new DoubleEllipse(ellipseA, ellipseB);
		session.setAttribute("doubleEllipse", doubleEllipse);

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(".");
	}

	public Person getCurrentUser(LdapTemplate ldapTemplate, String uid) {
		return ldapTemplate.findOne(query().where("uid").is(uid), Person.class);
	}
	
	public static class DoubleEllipse {
		Ellipse ellipseA;
		Ellipse ellipseB;
		public DoubleEllipse(Ellipse ellipseA, Ellipse ellipseB) {
			this.ellipseA = ellipseA;
			this.ellipseB = ellipseB;
		}
		public Ellipse getEllipseA() { return ellipseA; }
		public Ellipse getEllipseB() { return ellipseB; }
	}

	public static class Ellipse {
		String latitude;
		String longitude;
		String majorAxis;
		String minorAxis;
		String azimuth;
		public Ellipse(String latitude, String longitude, String majorAxis, String minorAxis, String azimuth) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.majorAxis = majorAxis;
			this.minorAxis = minorAxis;
			this.azimuth = azimuth;
		}
		public String getLatitude() {
			return latitude;
		}
		public String getLongitude() {
			return longitude;
		}
		public String getMajorAxis() {
			return majorAxis;
		}
		public String getMinorAxis() {
			return minorAxis;
		}
		public String getAzimuth() {
			return azimuth;
		}
	}
}