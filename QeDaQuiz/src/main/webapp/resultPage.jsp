<%@ page import="java.time.Instant" %>
<%@ page import="java.time.Duration" %>
<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.questions.Question" %>
<%@ page import="java.util.AbstractMap" %>
<%@ page import="java.util.List" %>
<%@ page import="quiz.history.Stat" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="static java.lang.Math.min" %>
<%@ page import="Constantas.Constantas" %><%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 09.07.2025
  Time: 21:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Results</title>
</head>
<body>
<%

  Instant startTime = (Instant) session.getAttribute("startTime");
  Instant endTime = Instant.now();
  Duration duration = Duration.between(startTime, endTime);
  long secondsTaken = duration.getSeconds();
  long minutesTaken = duration.toMinutes();
  session.removeAttribute("startTime");
  long rem = secondsTaken - minutesTaken*60;
  String qId = (String) session.getAttribute("quizId");
  Account user = (Account) session.getAttribute("user");
  QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
  UsersDao dbUsers = (UsersDao) request.getServletContext().getAttribute("accountDB");
  Quiz cur = db.getQuiz(Integer.parseInt(qId));
  HistoryDao dbHist = (HistoryDao) request.getServletContext().getAttribute("histDao");
  List<Stat> stats = dbHist.getQuizStats(Integer.parseInt(qId), false);
  int some = min(stats.size(), Constantas.TABLE_STATS_ENTRY);
%>

<h1><%=cur.getQuizName()%></h1>

<div class="results-container">
  <div class="score-section">
    <h1>Quiz Completed!</h1>
    <h2>Score: <%= request.getAttribute("quizScore") %></h2>
    <h2>Time: <%=minutesTaken%> min, <%=rem%> s</h2>
  </div>

  <%
    List<AbstractMap.SimpleEntry<String, Question>> answers =
            (List<AbstractMap.SimpleEntry<String, Question>>) request.getAttribute("userAnswers");

    if (answers != null) {
      for (AbstractMap.SimpleEntry<String, Question> entry : answers) {
        String userAnswer = entry.getKey();
        Question question = entry.getValue();
        String correctAnswer = question.getCorrectAnswers().get(0);
        boolean correct = correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
  %>
  <div class="question-block">
    <div class="question-text"><%= question.getPrompt() %></div>
    <div class="user-answer">
      Your Answer: <span class="<%= correct ? "correct-answer" : "wrong" %>"><%= userAnswer %></span>
    </div>
    <% if (!correct) { %>
    <div class="correct-answer">
      Correct Answer: <%= correctAnswer %>
    </div>
    <% } %>
  </div>
  <% } } else { %>
  <p>No answers found.</p>
  <% } %>
</div>
<div class="section">
  <h2>Top Performers of All Time</h2>
  <ul>
    <%for(int i = 0; i<some; i++){
      Stat curStat = stats.get(i);
      Account player = dbUsers.getUser(curStat.getUserId());
      int points = curStat.getPoints();
      int maxs = curStat.getMaxPoints();
    %>
    <li><%=player.getUsername()%> - <%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</li>
    <%
      }
    %>
    <!-- etc. -->
  </ul>
</div>
<div class="section">
  <a href="mainPage.jsp" class="start-quiz">Go to main page</a>
</div>
</body>
</html>

