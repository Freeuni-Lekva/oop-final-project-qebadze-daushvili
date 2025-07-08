<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="quiz.history.Stat" %>
<%@ page import="java.util.List" %>
<%@ page import="static java.lang.Math.min" %>
<%@ page import="Constantas.Constantas" %><%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 08.07.2025
  Time: 15:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%
        String sid =  request.getParameter("id");
        int id = Integer.parseInt(sid);
        Account user = (Account) session.getAttribute("user");
        QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
        UsersDao dbUsers = (UsersDao) request.getServletContext().getAttribute("accountDB");
        Quiz cur = db.getQuiz(id);
        Account creator = dbUsers.getUser(cur.getUserId());
        HistoryDao dbHist = (HistoryDao) request.getServletContext().getAttribute("histDao");
        List<Stat> stats = dbHist.getQuizStats(id, false);
        List<Stat> stats24 = dbHist.getQuizStats(id, true);
        int some = min(stats.size(), Constantas.TABLE_STATS_ENTRY);
        int some24 = min(stats24.size(), Constantas.TABLE_STATS_ENTRY);
        List<Stat> myStats = dbHist.getQuizStatsByUser(id, user.getId());
        int mySome = min(myStats.size(), Constantas.TABLE_STATS_ENTRY);
        float avgPoint = 0;
        float avgTime = 0;
        int attempts = 0;
        if(stats.size()>0) {
            avgTime = stats.get(0).getAvgTime();
            avgPoint = stats.get(0).getAvgScore();
            attempts = stats.get(0).getAttempts();
        }
    %>
    <div class="quiz-summary">
        <h1><%=cur.getQuizName()%></h1>

        <div class="section">
            <h2>Description</h2>
            <p><%=cur.getQuizDescription()%></p>
        </div>

        <div class="section creator-link">
            <h2>Created by</h2>
            <a href="userPage.jsp?userId=<%=creator.getId()%>"><%=creator.getUsername()%></a>
        </div>

        <div class="section">
            <h2>Your Past Performance</h2>
            <!-- Optional sorting options -->
            <label>Sort by:
                <select>
                    <option>Date</option>
                    <option>Percent Correct</option>
                    <option>Time Taken</option>
                </select>
            </label>
            <table>
                <tr>
                    <th>Date</th>
                    <th>Score (%)</th>
                    <th>Time Taken</th>
                </tr>

                <%for(int i =0; i<mySome; i++){
                    Stat curStat = myStats.get(i);
                    int points = curStat.getPoints();
                    int maxs = curStat.getMaxPoints();
                %>
                <tr>
                    <td><%=curStat.getLast()%></td>
                    <td><%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</td>
                    <td><%=curStat.getTime()%>s</td>
                </tr>
                <%
                }
                %>
                <!-- More rows here -->
            </table>
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
            <h2>Recent Test Takers </h2>
            <ul>
                <%for(int i = 0; i<some24; i++){
                    Stat curStat = stats24.get(i);
                    Account player = dbUsers.getUser(curStat.getUserId());
                    int points = curStat.getPoints();
                    int maxs = curStat.getMaxPoints();
                %>
                <li><%=player.getUsername()%> - <%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</li>
                <%
                    }
                %>
            </ul>
        </div>
        <%
            stats24.sort((q1, q2) -> Integer.compare(q2.getPoints(), q1.getPoints()));
        %>
        <div class="section">
            <h2>Top Performers in Last 24 Hours</h2>
            <ul>
                <%for(int i = 0; i<some24; i++){
                    Stat curStat = stats24.get(i);
                    Account player = dbUsers.getUser(curStat.getUserId());
                    int points = curStat.getPoints();
                    int maxs = curStat.getMaxPoints();
                %>
                <li><%=player.getUsername()%> - <%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</li>
                <%
                    }
                %>
            </ul>
        </div>

        <div class="section">
            <h2>Quiz Statistics</h2>
            <p>Average Score: <%=avgPoint%></p>
            <p>Average Time Taken: <%=avgTime%></p>
            <p>Total Attempts: <%=attempts%></p>
        </div>

        <div class="section">
            <a href="takeQuiz.jsp?id=<%=id%>" class="start-quiz">Start This Quiz</a>
        </div>
    </div>
</body>
</html>
