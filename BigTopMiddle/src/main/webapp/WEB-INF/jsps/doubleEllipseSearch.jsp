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
	<h1>Double Ellipse Intersection Search demo</h1>
	<div>
		<form method='GET' action='doubleEllipseSearch'>
			<section class="container">
				<div id='ellipseA' class="left">
					<label for="ellipseLatitudeA">Latitude: </label><input type='text' name='ellipseLatitudeA' value='${doubleEllipse.ellipseA.latitude}'><p>
					<label for="ellipseLongitudeA">Longitude: </label><input type='text' name='ellipseLongitudeA' value='${doubleEllipse.ellipseA.longitude}'><p>
					<label for="majoraxisA">Semi-Major Axis: </label><input type='text' name='majoraxisA' value='${doubleEllipse.ellipseA.majorAxis}'><p>
					<label for="minoraxisA">Semi-Minor Axis: </label><input type='text' name='minoraxisA' value='${doubleEllipse.ellipseA.minorAxis}'><p>
					<label for="azimuthA">Azimuth: </label><input type='text' name='azimuthA' value='${doubleEllipse.ellipseA.azimuth}'><p>
				</div>
				<div id='ellipseB' class="right">
					<label for="ellipseLatitudeB">Latitude: </label><input type='text' name='ellipseLatitudeB' value='${doubleEllipse.ellipseB.latitude}'><p>
					<label for="ellipseLongitudeB">Longitude: </label><input type='text' name='ellipseLongitudeB' value='${doubleEllipse.ellipseB.longitude}'><p>
					<label for="majoraxisB">Semi-Major Axis: </label><input type='text' name='majoraxisB' value='${doubleEllipse.ellipseB.majorAxis}'><p>
					<label for="minoraxisB">Semi-Minor Axis: </label><input type='text' name='minoraxisB' value='${doubleEllipse.ellipseB.minorAxis}'><p>
					<label for="azimuthB">Azimuth: </label><input type='text' name='azimuthB' value='${doubleEllipse.ellipseB.azimuth}'><p>
				</div>
				<br/>
				<input type='submit' value='Submit'>
			</section>
		</form>
	</div>
	<br/>
	<jsp:include page="/WEB-INF/jsps/footer.jsp"/>
</body>
</html>