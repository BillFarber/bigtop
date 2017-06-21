package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
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

import com.markLogic.bigTop.middle.CustomAuthenticationSuccessHandler;
import com.markLogic.bigTop.middle.CustomAuthenticationSuccessHandler.DoubleEllipse;
import com.markLogic.bigTop.middle.CustomAuthenticationSuccessHandler.Ellipse;
import com.markLogic.bigTop.middle.geospatial.StateJavascriptGenerator;
import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@Controller
@Configuration
public class DoubleEllipseController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(DoubleEllipse.class);
	private static StateJavascriptGenerator javascriptGenerator = StateJavascriptGenerator.getJavascriptGenerator();

	@RequestMapping(value = "/doubleEllipseSearch")
	public String searchGet(Model model, HttpServletRequest request) throws javax.naming.NamingException, IOException {
		Boolean noFormData = (request.getParameter("ellipseLatitudeA") == null);
		String view = "doubleEllipseSearch";
		if (noFormData) {
			logger.info("Double Ellipse Search load");
			HttpSession session = request.getSession();
			Person person = (Person) session.getAttribute("person");
			DoubleEllipse doubleEllipse = (DoubleEllipse) session.getAttribute("doubleEllipse");
			model.addAttribute("doubleEllipse", doubleEllipse);
			model.addAttribute("person", person);
		} else {
			logger.info("Double Ellipse Search search");
			HttpSession session = request.getSession();
			Person person = (Person) session.getAttribute("person");

			DoubleEllipse doubleEllipse = getDoubleEllipseFromRequest(request);
			session.setAttribute("doubleEllipse", doubleEllipse);
			
			List<String> resultUris = null;
			Float[][] verticesA = generateEllipseVertices(doubleEllipse.getEllipseA());
			Float[][] verticesB = generateEllipseVertices(doubleEllipse.getEllipseB());
			doubleEllipseSearch(model, request, verticesA, verticesB);

			model.addAttribute("person", person);
			model.addAttribute("resultUris", resultUris);
			view =  "states";
		}
		return view;
	}

	private DoubleEllipse getDoubleEllipseFromRequest(HttpServletRequest request) {
		String ellipseLatitudeParamA = request.getParameter("ellipseLatitudeA");
		String ellipseLongitudeParamA = request.getParameter("ellipseLongitudeA");
		String majoraxisParamA = request.getParameter("majoraxisA");
		String minoraxisParamA = request.getParameter("minoraxisA");
		String azimuthParamA = request.getParameter("azimuthA");
		String ellipseLatitudeParamB = request.getParameter("ellipseLatitudeB");
		String ellipseLongitudeParamB = request.getParameter("ellipseLongitudeB");
		String majoraxisParamB = request.getParameter("majoraxisB");
		String minoraxisParamB = request.getParameter("minoraxisB");
		String azimuthParamB = request.getParameter("azimuthB");
		Ellipse ellipseA = new CustomAuthenticationSuccessHandler.Ellipse(ellipseLatitudeParamA, ellipseLongitudeParamA, majoraxisParamA, minoraxisParamA, azimuthParamA);
		Ellipse ellipseB = new CustomAuthenticationSuccessHandler.Ellipse(ellipseLatitudeParamB, ellipseLongitudeParamB, majoraxisParamB, minoraxisParamB, azimuthParamB);
		return new CustomAuthenticationSuccessHandler.DoubleEllipse(ellipseA, ellipseB);
	}

	private List<String> doubleEllipseSearch(Model model, HttpServletRequest request, Float[][] verticesA,
			Float[][] verticesB) throws NamingException, IOException {
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		MarkLogicService mlService = new MarkLogicService(mlClient);

		List<String> resultUris = mlService.geoDoublePolygonSearch(verticesA, verticesB);
		javascriptGenerator.addStatePolygons(request, model, resultUris);

		String otherPolygonCommand = javascriptGenerator.getOtherPolygonCommandFromPolygonWkt(verticesA);
		otherPolygonCommand += "\n" + javascriptGenerator.getOtherPolygonCommandFromPolygonWkt(verticesB);
		model.addAttribute("otherPolygonCommand", otherPolygonCommand);
		logger.info("otherPolygonCommand: \n" + otherPolygonCommand);
		return resultUris;
	}

	private Float[][] generateEllipseVertices(Ellipse ellipse) {
		Float latitude = Float.parseFloat(ellipse.getLatitude());
		Float longitude = Float.parseFloat(ellipse.getLongitude());
		Float majorAxis = Float.parseFloat(ellipse.getMajorAxis());
		Float minorAxis = Float.parseFloat(ellipse.getMinorAxis());
		// Float azimuth = Float.parseFloat(ellipse.getAzimuth());
		Integer numVertices = 30;
		Float[][] vertices = new Float[numVertices + 1][2];
		for (int i = 0; i < numVertices; i++) {
			Float degrees = (360f / numVertices) * i;
			vertices[i][0] = (float) (latitude + (majorAxis * Math.cos(Math.toRadians(degrees))));
			vertices[i][1] = (float) (longitude + (minorAxis * Math.sin(Math.toRadians(degrees))));
			System.out.println("(" + vertices[i][0] + "," + vertices[i][1] + ")");
		}
		vertices[numVertices][0] = vertices[0][0];
		vertices[numVertices][1] = vertices[0][1];
		return vertices;
	}
}
