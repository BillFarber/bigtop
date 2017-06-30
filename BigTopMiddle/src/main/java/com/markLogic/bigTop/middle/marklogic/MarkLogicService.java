package com.markLogic.bigTop.middle.marklogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.marklogic.domain.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.GeospatialOperator;
import com.marklogic.client.query.StructuredQueryBuilder.Point;
import com.marklogic.client.query.StructuredQueryBuilder.Polygon;
import com.marklogic.client.query.StructuredQueryDefinition;

public class MarkLogicService {

	private static final Logger logger = LoggerFactory.getLogger(MarkLogicService.class);

	private DatabaseClient client;

	public MarkLogicService(DatabaseClient client) {
		this.client = client;
	}

	public List<String> readStateRegion(String uri) {
		JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
		JacksonHandle readHandle = new JacksonHandle();
		readHandle = jsonDocumentManager.read(uri, readHandle);
		JsonNode root = readHandle.get();
		return root.findValuesAsText("region");
	}

	public Product getProduct(String uri) {
		return readJacksonDocument(uri);
	}

	public List<String> search(String q) {
		QueryManager queryMgr = client.newQueryManager();
		StringQueryDefinition qd = queryMgr.newStringDefinition();
		qd.setCriteria(q);
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(qd, searchResults);

		List<String> resultUris = new ArrayList<String>();
		JsonNode results = searchResults.get().get("results");
		Iterator<JsonNode> resultIterator = results.elements();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			resultUris.add(result.get("uri").asText());
		}

		return resultUris;
	}

	public List<String> geoPointSearch(Float latitude, Float longitude) {
		List<String> resultUris = new ArrayList<String>();
		QueryManager queryMgr = client.newQueryManager();

		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = sqb.geospatial(
				sqb.geoRegionPath(sqb.pathIndex("//location/region"), StructuredQueryBuilder.CoordinateSystem.WGS84),
				GeospatialOperator.CONTAINS, sqb.point(latitude, longitude));
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(query, searchResults);
		JsonNode results = searchResults.get().get("results");
		Iterator<JsonNode> resultIterator = results.elements();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			resultUris.add(result.get("uri").asText());
		}

		return resultUris;
	}

	public List<String> geoBoxSearch(Float south, Float west, Float north, Float east) {
		List<String> resultUris = new ArrayList<String>();
		QueryManager queryMgr = client.newQueryManager();

		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = sqb.geospatial(
				sqb.geoRegionPath(sqb.pathIndex("//location/region"), StructuredQueryBuilder.CoordinateSystem.WGS84),
				GeospatialOperator.INTERSECTS, sqb.box(south, west, north, east));
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(query, searchResults);
		JsonNode results = searchResults.get().get("results");
		Iterator<JsonNode> resultIterator = results.elements();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			resultUris.add(result.get("uri").asText());
		}

		return resultUris;
	}

	public List<String> geoPolygonSearch(Float[][] vertices) {
		List<String> resultUris = new ArrayList<String>();
		QueryManager queryMgr = client.newQueryManager();

		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition query = sqb.geospatial(
				sqb.geoRegionPath(sqb.pathIndex("//location/region"), StructuredQueryBuilder.CoordinateSystem.WGS84),
				GeospatialOperator.INTERSECTS, createSQBPolygonFromVertices(sqb, vertices));
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(query, searchResults);
		JsonNode results = searchResults.get().get("results");
		Iterator<JsonNode> resultIterator = results.elements();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			resultUris.add(result.get("uri").asText());
		}

		return resultUris;
	}

	private Polygon createSQBPolygonFromVertices(StructuredQueryBuilder sqb, Float[][] vertices) {
		Point[] points = new Point[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			points[i] = sqb.point(vertices[i][0], vertices[i][1]);
		}
		return sqb.polygon(points);
	}

	private Product readJacksonDocument(String uri) {
		JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
		return jsonDocumentManager.readAs(uri, Product.class);
	}

	public List<String> geoDoublePolygonSearch(Float[][] verticesA, Float[][] verticesB) {
		List<String> resultUris = new ArrayList<String>();
		QueryManager queryMgr = client.newQueryManager();

		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder();
		StructuredQueryDefinition queryA = sqb.geospatial(
				sqb.geoRegionPath(sqb.pathIndex("//location/region"), StructuredQueryBuilder.CoordinateSystem.WGS84),
				GeospatialOperator.INTERSECTS, createSQBPolygonFromVertices(sqb, verticesA));
		StructuredQueryDefinition queryB = sqb.geospatial(
				sqb.geoRegionPath(sqb.pathIndex("//location/region"), StructuredQueryBuilder.CoordinateSystem.WGS84),
				GeospatialOperator.INTERSECTS, createSQBPolygonFromVertices(sqb, verticesB));
		StructuredQueryDefinition andQuery = sqb.and(queryA, queryB);
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(andQuery, searchResults);
		JsonNode results = searchResults.get().get("results");
		Iterator<JsonNode> resultIterator = results.elements();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			resultUris.add(result.get("uri").asText());
		}

		return resultUris;
	}

	public List<String> cacSearchViaTransform(String modulation, String minimumFrequency, String maximumFrequency) throws JsonProcessingException {
		logger.info("CAC search via Structured Query with Custom Transform: ");
		QueryManager queryMgr = client.newQueryManager();
		StructuredQueryBuilder sqb = queryMgr.newStructuredQueryBuilder("cacOptions");
		StructuredQueryDefinition modulationQuery = sqb.value(sqb.jsonProperty("modulation"), modulation);
		StructuredQueryDefinition minimumFrequencyQuery = sqb.range(sqb.jsonProperty("freq"), "long",
				StructuredQueryBuilder.Operator.GE, minimumFrequency);
		StructuredQueryDefinition maximumFrequencyQuery = sqb.range(sqb.jsonProperty("freq"), "long",
				StructuredQueryBuilder.Operator.LE, maximumFrequency);
		StructuredQueryDefinition nearQuery = sqb.near(2, 1, StructuredQueryBuilder.Ordering.UNORDERED, modulationQuery,
				minimumFrequencyQuery, maximumFrequencyQuery);
		JacksonHandle searchResults = new JacksonHandle();
		searchResults = queryMgr.search(nearQuery, searchResults);
		JsonNode results = searchResults.get().get("results");
		List<String> resultUris = new ArrayList<String>();
		Iterator<JsonNode> resultIterator = results.elements();
		ObjectMapper mapper = new ObjectMapper();
		while (resultIterator.hasNext()) {
			JsonNode result = resultIterator.next();
			String matches = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.get("matches"));
			resultUris.add(matches);
		}
		return resultUris;
	}

	public JsonNode cacSearchViaEndpoint(String modulation, String minimumFrequency, String maximumFrequency) throws JsonProcessingException {
		logger.info("CAC search via custom endpoint: ");
		CacSearchManager cacSearchManager = new CacSearchManager(client);
		JsonNode results = cacSearchManager.cacSearch(modulation, minimumFrequency, maximumFrequency);

		return results;
	}
}
