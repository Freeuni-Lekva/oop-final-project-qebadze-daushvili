<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Create New Quiz</title>
</head>
<body>
<% if(request.getAttribute("message") == null){%>
<h2>Create New Quiz</h2>
<%}else{%>
<h2><%=request.getAttribute("message")%></h2>
<%}%>

<p>Please enter quiz name, description and number of questions.</p>
<form method="POST" action="CreateQuizServlet">
  <input type="hidden" name="page" value="createQuiz">
  <input type="text" name="quizName" placeholder="Quiz Name">
  <br></br>
  <input type="text" name="quizDescription" placeholder="Quiz Description">
  <br></br>
  <input type="submit" name="startCreating" value="Start Creating">
</form>
</body>
</html>