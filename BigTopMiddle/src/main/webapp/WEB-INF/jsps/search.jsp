<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>BigTop Middle Demo</title>
</head>
<body>
	<h1>Welcome to the Spring/MarkLogic/LDAP demo, ${person.fullName}!</h1>
	<div>
		<form method='GET' action='search'>
			<input type='text' name='q' value=''><input type='submit'
				value='Submit'>
		</form>
	</div>
	<div>
		<h3>Search Results</h3>
		<c:if test="${not empty resultUris}">
			<ul>
				<c:forEach var="listValue" items="${resultUris}">
					<li>${listValue}</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
	<div>
		<a href='.'>Home</a>
	</div>
	<div>
		<a href='logout'>Logout</a>
	</div>
</body>
</html>