<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 25.06.2025
  Time: 15:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create new account</title>
    <link rel="stylesheet" type="text/css" href="/css/entrance.css">
</head>
<body>
<% if(request.getAttribute("message") == null){%>
<h2>Create a new account</h2>
<%}else{%>
<h2><%=request.getAttribute("message")%></h2>
<%}%>

<p>please, enter your name and password</p>

<form method="POST" action="RegistrationServlet">
    <input type="text" name="username" placeholder="username">
    <br></br>
    <input type="password" name="password" placeholder="password">
    <br></br>
    <input type="text" name="picture" placeholder="picture">
    <br></br>
    <input type="submit" value="Login">
</form>

</body>
</html>
