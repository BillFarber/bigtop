<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>BigTop Middle Demo</title>
</head>
<body>
	<h1>Welcome to the Spring/MarkLogic/LDAP demo, ${person.fullName}!</h1>
	<div>
		<ul>
		<li>${person.dn}</li>
		<li>${person.firstName}</li>
		<li>${person.lastName}</li>
		<li>${person.fullName}</li>
		<li>${person.cn}</li>
		<li>${person.uid}</li>
		</ul>
	</div>
	<div>
		<a href='${searchPath}'>Search Page</a>
	</div>
	<div>
		<a href='${logoutPath}'>Logout</a>
	</div>
</body>
</html>