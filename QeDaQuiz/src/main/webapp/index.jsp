<html>

<head>
    <title>QeDaQuiz login</title>
    <link rel="stylesheet" type="text/css" href="/css/entrance.css">
</head>
<body>
<h2>Welcome to QeDa!</h2>

<% if(request.getAttribute("message") == null){%>
<p>please, enter your name and password</p>
<%}else{%>
<p><%=request.getAttribute("message")%></p>
<%}%>

<form method="POST" action="LoginServlet">
    <input type="text" name="username" placeholder="username">
    <br></br>
    <input type="password" name="password" placeholder="password">
    <br></br>
    <input type="submit" value="Login">
</form>
<a href="/registration.jsp">Create new account</a>
</body>
</html>
