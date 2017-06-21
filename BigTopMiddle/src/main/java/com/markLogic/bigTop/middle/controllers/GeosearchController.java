package com.markLogic.bigTop.middle.controllers;

import java.io.IOException;
import java.util.ArrayList;
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

import com.markLogic.bigTop.middle.geospatial.StateJavascriptGenerator;
import com.markLogic.bigTop.middle.ldapDomain.Person;
import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

@Controller
@Configuration
public class GeosearchController {
	@Autowired
	DefaultSpringSecurityContextSource contextSource;

	private static final Logger logger = LoggerFactory.getLogger(GeosearchController.class);
	private static StateJavascriptGenerator javascriptGenerator = StateJavascriptGenerator.getJavascriptGenerator();

	@RequestMapping("/geosearch")
	public String searchPost(Model model, HttpServletRequest request) throws javax.naming.NamingException, IOException {
		String view = "geosearch";

		HttpSession session = request.getSession();
		Person person = (Person) session.getAttribute("person");

		model.addAttribute("latitude", "42.4");
		model.addAttribute("longitude", "-72.6");

		model.addAttribute("south", "41.4");
		model.addAttribute("west", "-72.6");
		model.addAttribute("north", "42.6");
		model.addAttribute("east", "-72.4");

		model.addAttribute("latitudes", "40 40 42 42 40");
		model.addAttribute("longitudes", "-70 -72 -72 -70 -70");

		model.addAttribute("ellipseLatitude", "42.4");
		model.addAttribute("ellipseLongitude", "-72.6");
		model.addAttribute("majoraxis", "20");
		model.addAttribute("minoraxis", "40");
		model.addAttribute("azimuth", "0");
		model.addAttribute("pointRadio", "CHECKED");
		model.addAttribute("boxRadio", "");
		model.addAttribute("polygonRadio", "");
		model.addAttribute("ellipseRadio", "");
		model.addAttribute("searchType", "pointRadio");

		List<String> resultUris = null;
		String searchType = request.getParameter("searchType");
		if (searchType != null) {
			if (searchType.equals("point")) {
				String latitudeParam = request.getParameter("latitude");
				String longitudeParam = request.getParameter("longitude");
				model.addAttribute("latitude", latitudeParam);
				model.addAttribute("longitude", longitudeParam);
				if ((latitudeParam != null) && (longitudeParam != null)) {
					if ((!latitudeParam.isEmpty()) && (!longitudeParam.isEmpty())) {
						runGeospatialPointSearch(model, request, latitudeParam, longitudeParam);
						view = "states";
					}
				}
			} else if (searchType.equals("box")) {
				model.addAttribute("pointChecked", "");
				model.addAttribute("boxChecked", "CHECKED");
				model.addAttribute("searchType", "boxRadio");
				String southParam = request.getParameter("south");
				String westParam = request.getParameter("west");
				String northParam = request.getParameter("north");
				String eastParam = request.getParameter("east");
				model.addAttribute("south", southParam);
				model.addAttribute("west", westParam);
				model.addAttribute("north", northParam);
				model.addAttribute("east", eastParam);
				if ((southParam != null) && (westParam != null) && (northParam != null) && (eastParam != null)) {
					if ((!southParam.isEmpty()) && (!westParam.isEmpty()) && (!northParam.isEmpty())
							&& (!eastParam.isEmpty())) {
						runGeospatialBoxSearch(model, request, southParam, westParam, northParam, eastParam);
						view = "states";
					}
				}
			} else if (searchType.equals("polygon")) {
				model.addAttribute("pointChecked", "");
				model.addAttribute("polygonChecked", "CHECKED");
				model.addAttribute("searchType", "polygonRadio");
				String latitudesParam = request.getParameter("latitudes");
				String longitudesParam = request.getParameter("longitudes");
				model.addAttribute("latitudes", latitudesParam);
				model.addAttribute("longitudes", longitudesParam);
				if ((latitudesParam != null) && (longitudesParam != null)) {
					if ((!latitudesParam.isEmpty()) && (!longitudesParam.isEmpty())) {
						runGeospatialPolygonSearch(model, request, latitudesParam, longitudesParam);
						view = "states";
					}
				}
			} else if (searchType.equals("ellipse")) {
				model.addAttribute("pointChecked", "");
				model.addAttribute("ellipseChecked", "CHECKED");
				model.addAttribute("searchType", "ellipseRadio");
				String ellipseLatitudeParam = request.getParameter("ellipseLatitude");
				String ellipseLongitudeParam = request.getParameter("ellipseLongitude");
				String majoraxisParam = request.getParameter("majoraxis");
				String minoraxisParam = request.getParameter("minoraxis");
				String azimuthParam = request.getParameter("azimuth");
				model.addAttribute("ellipseLatitude", ellipseLatitudeParam);
				model.addAttribute("ellipseLongitude", ellipseLongitudeParam);
				model.addAttribute("majoraxis", majoraxisParam);
				model.addAttribute("minoraxis", minoraxisParam);
				model.addAttribute("azimuth", azimuthParam);
				if ((ellipseLatitudeParam != null) && (ellipseLongitudeParam != null) && (majoraxisParam != null) && (minoraxisParam != null) && (azimuthParam != null)) {
					if ((!ellipseLatitudeParam.isEmpty()) && (!ellipseLongitudeParam.isEmpty()) && (!majoraxisParam.isEmpty()) && (!minoraxisParam.isEmpty()) && (!azimuthParam.isEmpty())) {
						runGeospatialEllipseSearch(model, request, ellipseLatitudeParam, ellipseLongitudeParam, majoraxisParam, minoraxisParam, azimuthParam);
						view = "states";
					}
				}
			}
		}

		model.addAttribute("person", person);
		model.addAttribute("resultUris", resultUris);
		return view;
	}
	
	private List<String> runGeospatialPointSearch(Model model, HttpServletRequest request, String latitudeParam, String longitudeParam) throws NamingException, IOException {
		Float latitude = Float.parseFloat(latitudeParam);
		Float longitude = Float.parseFloat(longitudeParam);
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris = mlService.geoPointSearch(latitude, longitude);
		javascriptGenerator.addStatePolygons(request, model, resultUris);
		logger.info("Found " + resultUris.size() + " matches");
		return resultUris;
	}
	
	private List<String> runGeospatialBoxSearch(Model model, HttpServletRequest request, String southParam, String westParam, String northParam, String eastParam) throws NamingException, IOException {
		Float south = Float.parseFloat(southParam);
		Float west = Float.parseFloat(westParam);
		Float north = Float.parseFloat(northParam);
		Float east = Float.parseFloat(eastParam);
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris = mlService.geoBoxSearch(south, west, north, east);
		resultUris = mlService.geoBoxSearch(south, west, north, east);
		javascriptGenerator.addStatePolygons(request, model, resultUris);
		logger.info("Found " + resultUris.size() + " matches");
		return resultUris;
	}
	
	private List<String> runGeospatialPolygonSearch(Model model, HttpServletRequest request, String latitudesParam, String longitudesParam) throws NamingException, IOException {
		String[] latitudeStringArray = latitudesParam.split(" ");
		Integer numLatitudes = latitudeStringArray.length;
		Float[] latitudes = new Float[numLatitudes];
		for (int i = 0; i < numLatitudes; i++) {
			latitudes[i] = Float.parseFloat(latitudeStringArray[i]);
		}

		String[] longitudeStringArray = longitudesParam.split(" ");
		Integer numLongitudes = longitudeStringArray.length;
		Float[] longitudes = new Float[numLongitudes];
		for (int i = 0; i < numLongitudes; i++) {
			longitudes[i] = Float.parseFloat(longitudeStringArray[i]);
		}
		
		List<String> resultUris = new ArrayList<String>();
		if (numLatitudes == numLongitudes) {
			DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
			MarkLogicService mlService = new MarkLogicService(mlClient);
			Float[][] vertices = new Float[numLatitudes][2];
			for (int i = 0; i < numLatitudes; i++) {
				vertices[i][0] = latitudes[i];
				vertices[i][1] = longitudes[i];
			}
			resultUris = mlService.geoPolygonSearch(vertices);
			javascriptGenerator.addStatePolygons(request, model, resultUris);
			logger.info("Found " + resultUris.size() + " matches");
		} else {
			logger.info("The number of values in latitudes and longitudes must match");
		}
		return resultUris;
	}
	
	private List<String> runGeospatialEllipseSearch(Model model, HttpServletRequest request, String ellipseLatitudeParam, String ellipseLongitudeParam, String majoraxisParam, String minoraxisParam, String azimuthParam) throws NamingException, IOException {
		Float ellipseLatitude = Float.parseFloat(ellipseLatitudeParam);
		Float ellipseLongitude = Float.parseFloat(ellipseLongitudeParam);
		Float majorAxis = Float.parseFloat(majoraxisParam);
		Float minorAxis = Float.parseFloat(minoraxisParam);
		Float azimuth = Float.parseFloat(azimuthParam);
		Float[][] vertices = generateEllipseVertices(ellipseLatitude, ellipseLongitude, majorAxis, minorAxis, azimuth);
		
		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		MarkLogicService mlService = new MarkLogicService(mlClient);
		List<String> resultUris  = mlService.geoPolygonSearch(vertices);
		javascriptGenerator.addStatePolygons(request, model, resultUris);
		return resultUris;
	}

	private Float[][] generateEllipseVertices(Float latitude, Float longitude, Float majorAxis, Float minorAxis, Float azimuth) {
		Integer numVertices = 30;
		Float[][] vertices = new Float[numVertices][2];
		for (int i = 0; i < numVertices; i++) {
			Float degrees = (360f / numVertices) * i;
			vertices[i][0] = (float) (latitude + (majorAxis  * Math.cos(Math.toRadians(degrees))));
			vertices[i][1] = (float) (longitude + (minorAxis * Math.sin(Math.toRadians(degrees))));
			System.out.println("("+vertices[i][0]+","+vertices[i][1]+")");
		}
		return vertices;
	}
}
