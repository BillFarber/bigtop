package com.markLogic.bigTop.middle.marklogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.extensions.ResourceManager;
import com.marklogic.client.extensions.ResourceServices;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.util.RequestParameters;

public class CacSearchManager extends ResourceManager {

	static final public String CAC_SEARCH_EXTENSION_NAME = "cac-search";

	public CacSearchManager(DatabaseClient client) {
		super();
		client.init(CAC_SEARCH_EXTENSION_NAME, this);
	}

	public JsonNode cacSearch(String modulation, String minimumFrequency, String maximumFrequency) throws JsonProcessingException {
		// get the initialized service object from the base class
		ResourceServices services = getServices();

		// Build up the set of parameters for the service call
		RequestParameters params = new RequestParameters();
		params.add("service", CAC_SEARCH_EXTENSION_NAME);
		params.add("modulation", modulation);
		params.add("minimumFrequency", minimumFrequency);
		params.add("maximumFrequency", maximumFrequency);
		JacksonHandle readHandle = new JacksonHandle();
		readHandle = services.get(params, readHandle);

		return readHandle.get();
	}
}
