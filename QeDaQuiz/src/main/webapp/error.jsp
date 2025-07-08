<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>QeDaQuiz Error</title>
    <link rel="stylesheet" type="text/css" href="/css/entrance.css">
</head>
<body>
<h2>Oops! Something went wrong.</h2>

<% if (request.getAttribute("error") == null) { %>
<p>An unexpected error occurred. Please try again.</p>
<% } else { %>
<p><%= request.getAttribute("error") %></p>
<% } %>

<a href="javascript:history.back()">Go Back</a>
</body>
</html>
