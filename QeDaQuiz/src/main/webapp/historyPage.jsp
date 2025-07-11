<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="quiz.history.Stat" %>
<%@ page import="java.util.List" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="Daos.UsersDao" %><%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 10.07.2025
  Time: 23:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>History</title>
  <link rel="stylesheet" href="/css/history.css">
</head>
<body>
<%
  int userId = Integer.parseInt(request.getParameter("userId"));
  UsersDao userDao=(UsersDao) request.getServletContext().getAttribute("accountDB");
  Account user = userDao.getUser(userId);
  HistoryDao dbHist = (HistoryDao) request.getServletContext().getAttribute("histDao");
  List<Stat> stats = dbHist.getQuizStatsByUser(user.getId());
  QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
  Collections.sort(stats, new Comparator<Stat>() {
    public int compare(Stat s1, Stat s2) {
      return s2.getLast().compareTo(s1.getLast());
    }
  });
%>
<div class="history-section">
  <h2>History</h2>
  <div style="margin-bottom: 20px;">
    <a href="MainPageServlet" class="start-quiz">← Back to Main Page</a>
  </div>
  <ul class="history-list">
    <% for(Stat stat : stats) {
        Quiz cur = db.getQuiz(stat.getQuizId());
        stat.setMaxPoints(cur.getQuestions().size());
    %>
    <li class="history-item">
      <div class="quiz-name"><%= cur.getQuizName() %></div>
      <div class="quiz-score">Score: <strong><%=stat.getPoints()%>/<%=stat.getMaxPoints()%>(<%=stat.getPercent()%>)</strong></div>
      <div class="quiz-time">Taken on: <%= stat.getTime() %>s</div>
      <div class="date"><%= stat.getLast()%></div>
    </li>
    <% } %>
  </ul>
</div>


</body>
</html>
