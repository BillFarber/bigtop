<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>BigTop Middle Demo</title>
</head>
<body>
	<h1>CAC Search demo</h1>
	<div>
		<form method='GET' action='cacSearch'>
			<label for="modulation">Modulation: </label><input type='text' name='modulation' value='${modulation}'><br/>
			<label for="minimumFrequency">Minimum Frequency: </label><input type='text' name='minimumFrequency' value='${minimumFrequency}'><br/>
			<label for="maximumFrequency">Maximum Frequency: </label><input type='text' name='maximumFrequency' value='${maximumFrequency}'><br/>
			<input type='submit' value='Submit'><br/>
		</form>
	</div>
	<div>
		<h3>Search Results from results transformation technique</h3>
		<c:if test="${not empty resultUris}">
			<ul>
				<c:forEach var="listValue" items="${resultUris}">
					<li>${listValue}</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
	<div>
		<h3>Search Results from results custom endpoint technique</h3>
		<c:if test="${not empty endpointResults}">
			<ul>
				<c:forEach var="listValue" items="${endpointResults}">
					<li>${listValue}</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
	<br/>
	<jsp:include page="/WEB-INF/jsps/footer.jsp"/>
</body>
</html>