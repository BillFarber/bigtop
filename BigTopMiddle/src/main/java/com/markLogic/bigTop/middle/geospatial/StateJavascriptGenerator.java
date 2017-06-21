package com.markLogic.bigTop.middle.geospatial;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import com.markLogic.bigTop.middle.marklogic.MarkLogicService;
import com.marklogic.client.DatabaseClient;

public class StateJavascriptGenerator {

	private static final Logger logger = LoggerFactory.getLogger(StateJavascriptGenerator.class);

	public static final String BASE_URI = "/geo/states/";
	public static final String[] STATES = { "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
			"Connecticut", "Delaware", "Florida", "Georgia", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas",
			"Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
			"Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
			"North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
			"South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
			"West Virginia", "Wisconsin", "Wyoming" };

	private static StateJavascriptGenerator javascriptGenerator = null;
	private StateJavascriptGenerator() {}
	
	public static StateJavascriptGenerator getJavascriptGenerator() {
		if (javascriptGenerator == null) {
			javascriptGenerator = new StateJavascriptGenerator();
		}
		return javascriptGenerator;
	}
	
	public String addStatePolygons(HttpServletRequest request, Model model, List<String> resultUris) throws javax.naming.NamingException, IOException {
		logger.info("Drawing states.");

		DatabaseClient mlClient = (DatabaseClient) request.getSession().getAttribute("mlclient");
		MarkLogicService mlService = new MarkLogicService(mlClient);
		
		String allStatesCommands = "";
		for (String state : STATES) {
			String stateURI = BASE_URI + state + ".json";
			List<String> stateRegionWKTs = mlService.readStateRegion(stateURI);
			for (String regionWKT : stateRegionWKTs) {
				String fillStyle = "white";
				if (resultUris.contains(stateURI)) {
					fillStyle = "red";
				}
				String statePolygonCommand = getStatePolygonCommandFromPolygonWkt(regionWKT, fillStyle);
				allStatesCommands += "\n" + statePolygonCommand;
			}
		}
		model.addAttribute("statePolygonCommand", allStatesCommands);

		return "states";
	}

	public String getStatePolygonCommandFromPolygonWkt(String polygonWKT, String fillStyle) {
		String verticesString = polygonWKT.replace("POLYGON", "").replace("((", "").replace("))", "");
		String[] verticesStringArray = verticesString.split(",");
		Integer numVertices = verticesStringArray.length;
		String jsonVertices = "var points = [";
		for (int i = 0; i < numVertices; i++) {
			String[] vertexArray = verticesStringArray[i].split(" ");
			Float longitudeRaw = Float.parseFloat(vertexArray[0]);
			Float latitudeRaw = Float.parseFloat(vertexArray[1]);
			Float longitude = (longitudeRaw * 1) * 19 + 3200;
			Float latitude = (latitudeRaw * -1) * 19 + 1400;
			jsonVertices += "{y:" + latitude + ",x:" + longitude + "}";
			if (i < numVertices - 1) {
				jsonVertices += ",";
			}
		}
		jsonVertices += "];";
		logger.info("State vertices: " + jsonVertices);
		return jsonVertices + "\ngenericPolygon(context, points, '" + fillStyle + "');";
	}

	public String getOtherPolygonCommandFromPolygonWkt(Float[][] vertices) {
		Integer numVertices = vertices.length;
		String jsonVertices = "var points = [";
		for (int i = 0; i < numVertices; i++) {
			Float[] vertexArray = vertices[i];
			Float longitudeRaw = vertexArray[1];
			Float latitudeRaw = vertexArray[0];
			Float longitude = (longitudeRaw * 1) * 19 + 3200;
			Float latitude = (latitudeRaw * -1) * 19 + 1400;
			jsonVertices += "{y:" + latitude + ",x:" + longitude + "}";
			if (i < numVertices - 1) {
				jsonVertices += ",";
			}
		}
		jsonVertices += "];";
		logger.info("Polygon vertices: " + jsonVertices);
		return jsonVertices + "\ngenericPolygon(context, points, '');";
	}

}
