package com.markLogic.bigTop.middle.marklogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markLogic.bigTop.middle.marklogic.domain.Product;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

public class MarkLogicService {

    private static ObjectMapper mapper = new ObjectMapper();
    private DatabaseClient client;

    public MarkLogicService(DatabaseClient client) {
    	this.client = client;
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

    private Product readJacksonDocument(String uri) {
        JSONDocumentManager jsonDocumentManager = client.newJSONDocumentManager();
        return jsonDocumentManager.readAs(uri, Product.class);
    }

    public void prettyPrintJackson(JacksonHandle searchResults) {
        try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(searchResults.get()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }
}
