<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<link rel='stylesheet' href='css/bigtop.css' type='text/css' />
<title>BigTop Middle Demo</title>
</head>
<body>
	<h1>Welcome to the Spring/MarkLogic/LDAP demo, ${person.fullName}!</h1>
	<div>
		<form method='GET' action='geosearch'>
        	<input id="pointRadio" TYPE="radio" name="searchType" value="point" onclick="showPointElements()" ${pointRadio}>Point</input>
            <input id="boxRadio" TYPE="radio" name="searchType" value="box" onclick="showBoxElements()" ${boxRadio}>Box</input>
            <input id="ellipseRadio" TYPE="radio" name="searchType" value="ellipse" onclick="showEllipseElements()" ${ellipseRadio}>Ellipse</input>
            <input id="polygonRadio" TYPE="radio" name="searchType" value="polygon" onclick="showPolygonElements()" ${polygonRadio}>Polygon</input>
			<br/>
			<div id='point'>
				<label for="latitude">Latitude: </label><input type='text' name='latitude' value='${latitude}'><p>
				<label for="latitude">Longitude: </label><input type='text' name='longitude' value='${longitude}'><p>
			</div>
			<div id='box'>
				<label for="south">South: </label><input type='text' name='south' value='${south}'><p>
				<label for="west">West : </label><input type='text' name='west' value='${west}'><p>
				<label for="north">North: </label><input type='text' name='north' value='${north}'><p>
				<label for="east">East : </label><input type='text' name='east' value='${east}'><p>
			</div>
			<div id='polygon'>
				<label for="latitudes">Latitudes : </label><input id='polygonLatitude' type='text' name='latitudes' value='${latitudes}'><p>
				<label for="longitudes">Longitudes: </label><input id='polygonLongitude' type='text' name='longitudes' value='${longitudes}'><p>
			</div>
			<div id='ellipse'>
				<label for="ellipseLatitude">Latitude: </label><input type='text' name='ellipseLatitude' value='${ellipseLatitude}'><p>
				<label for="ellipseLongitude">Longitude: </label><input type='text' name='ellipseLongitude' value='${ellipseLongitude}'><p>
				<label for="majoraxis">Semi-Major Axis: </label><input type='text' name='majoraxis' value='${majoraxis}'><p>
				<label for="minoraxis">Semi-Minor Axis: </label><input type='text' name='minoraxis' value='${minoraxis}'><p>
				<label for="azimuth">Azimuth: </label><input type='text' name='azimuth' value='${azimuth}'><p>
			</div>
			<input type='submit' value='Submit'>
		</form>
	</div>
	<div>
		<h3>Geospatial Search Results</h3>
		<c:if test="${not empty resultUris}">
			<ul>
				<c:forEach var="listValue" items="${resultUris}">
					<li>${listValue}</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
	<br/>
	<jsp:include page="/WEB-INF/jsps/footer.jsp"/>
	<script type="text/javascript" src="js/bigtop.js"></script>
	<script type="text/javascript">
		initialize("${searchType}");
	</script>
</body>
</html>