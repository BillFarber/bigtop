function get(context, params) {
	xdmp.log("cacSearch GET");
	
	var modulation = "BPSK";
	var minimumFrequency = "1719272563";
	var maximumFrequency = "1740926229";
	if (params.hasOwnProperty("modulation")) {
		modulation = params["modulation"];
	}
	if (params.hasOwnProperty("minimumFrequency")) {
		minimumFrequency = params["minimumFrequency"];
	}
	if (params.hasOwnProperty("maximumFrequency")) {
		maximumFrequency = params["maximumFrequency"];
	}

	var searchResults = cts.search(
		cts.nearQuery([
			cts.jsonPropertyValueQuery("modulation", modulation),
			cts.jsonPropertyRangeQuery("freq", ">=", minimumFrequency),
			cts.jsonPropertyRangeQuery("freq", "<=", maximumFrequency)
		], 2)
	).toArray();
	
	var xpath = "/store/uiSpectrums/*[signals/modulation='"+modulation+"' and signals/freq/data() > " + minimumFrequency + " and signals/freq/data() < " + maximumFrequency + "]";
	var processedResults = [];
	for (var i in searchResults) {
		var subTree = searchResults[i].xpath(xpath).toArray();
		for (var j in subTree) {
			processedResults.push(subTree[j]);
		}
	}
	xdmp.log("cacSearch GET finished");

	// Return a Sequence to return multiple documents
	context.outputTypes = [ 'application/json' ];
	context.outputStatus = [200, 'OK'];
	return processedResults;
};


// Include an export for each method supported by your extension.
exports.GET = get;