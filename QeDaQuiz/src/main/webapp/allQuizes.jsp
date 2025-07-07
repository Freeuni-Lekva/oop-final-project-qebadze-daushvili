<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 07.07.2025
  Time: 15:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="css/allQuizes.css">
</head>
<body>
<form method="POST" action="AllQuizServlet">
    <input type="submit" name = "action" value="Rank by release date">
    <input type="submit" name = "action" value="Rank by popularity">
</form>
<div class="recent-quizes">
<%
    ArrayList<Quiz> allQuizes = null;
    if(request.getAttribute("list") == null) {
        QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
        allQuizes = db.getQuizes();
    }else{
        allQuizes = (ArrayList<Quiz>) request.getAttribute("list");
    }
    for(int i = 0; i<allQuizes.size(); i++){
        Quiz quiz = allQuizes.get(i);
%>
    <li><a href="QuizDetails.jsp?id=<%=quiz.getQuizId()%>" style = "font-size: 20px;"><%= quiz.getQuizName()%></a></li>
    <p style="margin-bottom: 20px;"><%=quiz.getQuizDescription()%></p>

<%
    }
%>
</div>
</body>
</html>
