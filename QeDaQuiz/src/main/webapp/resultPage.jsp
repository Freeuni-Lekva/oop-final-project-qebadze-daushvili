<%@ page import="java.time.Instant" %>
<%@ page import="java.time.Duration" %>
<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.questions.Question" %>
<%@ page import="quiz.history.Stat" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="static java.lang.Math.min" %>
<%@ page import="Constantas.Constantas" %>
<%@ page import="org.junit.runner.manipulation.Ordering" %>
<%@ page import="Listeners.ContextListener" %>
<%@ page import="Daos.CommunicationDao" %>
<%@ page import="java.util.*" %><%--
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
    <link rel="stylesheet" href="/css/quizPage.css">
</head>
<body>
<%
  Instant startTime = (Instant) session.getAttribute("startTime");
  Instant endTime = (Instant) session.getAttribute("endTime");
  Duration duration = Duration.between(startTime, endTime);
  long secondsTaken = duration.getSeconds();
  long minutesTaken = duration.toMinutes();
  session.removeAttribute("startTime");
  long rem = secondsTaken - minutesTaken*60;
  int qId = (Integer) session.getAttribute("quizId");
  Account user = (Account) session.getAttribute("user");
  QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
  UsersDao dbUsers = (UsersDao) request.getServletContext().getAttribute("accountDB");
  Quiz cur = db.getQuiz(qId);
  HistoryDao dbHist = (HistoryDao) request.getServletContext().getAttribute("histDao");
  List<Stat> stats = dbHist.getQuizStats(qId, false);
  int some = min(stats.size(), Constantas.TABLE_STATS_ENTRY);
%>

<h1><%=cur.getQuizName()%></h1>

<div class="results-container">
  <div class="score-section">
    <h1>Quiz Completed!</h1>
    <h2>Score: <%= session.getAttribute("quizScore") %></h2>
    <h2>Time: <%=minutesTaken%> min, <%=rem%> s</h2>
  </div>

  <%
    List<AbstractMap.SimpleEntry<String, Question>> answers =
            (List<AbstractMap.SimpleEntry<String, Question>>) session.getAttribute("userAnswers");

    if (answers != null) {
      for (AbstractMap.SimpleEntry<String, Question> entry : answers) {
        String userAnswer = entry.getKey();
        Question question = entry.getValue();
        List<String> correctAnswer = question.getCorrectAnswers();

        boolean correct = false;
        for(int j = 0; j<correctAnswer.size(); j++) {
          if(correctAnswer.get(j).trim().equalsIgnoreCase(userAnswer.trim())) correct = true;
        }
  %>
  <div class="question-block">
    <div class="question-text"><%= question.getPrompt() %></div>
    <div class="user-answer">
      Your Answer: <span class="<%= correct ? "correct-answer" : "wrong" %>"><%= userAnswer %></span>
    </div>
    <% if (!correct) { %>
    <div class="correct-answer">
      Correct Answer: <%= correctAnswer.get(0) %>
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
    <%
      Collections.sort(stats, new Comparator<Stat>() {
      public int compare(Stat s1, Stat s2) {
        return Integer.compare(s2.getPoints(), s1.getPoints());
      }
    });
      for(int i = 0; i<some; i++){
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

  <a href="MainPageServlet" class="start-quiz">Go to main page</a>
</div>
</body>
</html>

