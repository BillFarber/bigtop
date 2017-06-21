'use strict';
declareUpdate();
var geojson = require('/MarkLogic/geospatial/geojson.xqy');

// Use this in qconsole to run
// 'use strict';
// declareUpdate();
// var transform = require('/transforms/transformStates.sjs');
// transform.extractStates();

var docOptions = {
	permissions : [
		xdmp.permission("BigTopUsersRole", "read"),
		xdmp.permission("BigTopAdminRole", "update")
	],
	collections : "http://com.marklogic/bigtop/geo"
};

function extractStates(content) {
	var statesDoc = cts.doc("/geo/terrain/statesGeoJSON.json").root;
	var states = statesDoc.features;
	var numStates = states.length;
	for (var i = 0; i < numStates; i++) {
  		var stateGeometry = states[i].geometry;
  		var stateName = states[i].properties.NAME;
		var isMultipolygon = (stateGeometry.type == "MultiPolygon");
  		var wktPolygons = geojson.parseGeojson(stateGeometry);

		var locations = [];
		if (isMultipolygon) {
			var polygonArray = wktPolygons.toArray();
			var numPolygons = polygonArray.length;
			for (var i = 0; i < numPolygons; i++) {
				var location = {
					"location" : {
						"region" : polygonArray[i]
					}
				};
				locations.push(location);
			}
		} else {
			var location = {
				"location" : {
					"region" : wktPolygons
				}
			};
			locations.push(location);
		}

  		var statePolygonWktFromGeoJson = {
    		"name": stateName,
    		"locations" : locations
  		};
  		var stateUri = "/geo/states/" + stateName + ".json";
  		xdmp.documentInsert(stateUri, statePolygonWktFromGeoJson, docOptions);
	};
};

exports.extractStates = extractStates;