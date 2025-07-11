<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="quiz.history.Stat" %>
<%@ page import="java.util.List" %>
<%@ page import="static java.lang.Math.min" %>
<%@ page import="Constantas.Constantas" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %><%--
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
    <link rel="stylesheet" href="/css/quizPage.css">
</head>
<body>
    <%

        String sid =  request.getParameter("id");
        if(sid != null){
            session.setAttribute("quizId", sid);
        }else{
            sid = (String) session.getAttribute("quizId");
        }
        if(session.getAttribute("userAnswers") != null) {
            session.removeAttribute("userAnswers");
        }
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
        int maxs = cur.getQuestions().size();
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
        <div style="margin-bottom: 20px;">
            <a href="MainPageServlet" class="start-quiz">← Back to Main Page</a>
        </div>
        <div class="section">
            <h2>Description</h2>
            <p><%=cur.getQuizDescription()%></p>
        </div>

        <div class="section creator-link">
            <h2>Created by</h2>
            <a href="userProfile.jsp?userId=<%=creator.getId()%>"><%=creator.getUsername()%></a>
        </div>

        <div class="section">
            <h2>Your Past Performance</h2>
            <!-- Optional sorting options -->
            <label>Sort by:
                <form method="POST" action="QuizPageServlet">
                    <input type="hidden" name="quizId" value="<%=id%>">
                    <label>Sort by:
                        <select name="sortBy" onchange="this.form.submit()">
                            <option value="date" <%= "date".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Date</option>
                            <option value="percent" <%= "percent".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Percent Correct</option>
                            <option value="time" <%= "time".equals(request.getParameter("sortBy")) ? "selected" : "" %>>Time Taken</option>
                        </select>
                    </label>
                </form>
            </label>
            <table>
                <tr>
                    <th>Date</th>
                    <th>Score (%)</th>
                    <th>Time Taken</th>
                </tr>

                <%
                    if(request.getAttribute("myStats") != null){
                        myStats = (List<Stat>) request.getAttribute("myStats");
                    }
                    for(int i =0; i<mySome; i++){
                        Stat curStat = myStats.get(i);
                        int points = curStat.getPoints();
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
            <h2>Recent Test Takers </h2>
            <ul>
                <%for(int i = 0; i<some24; i++){
                    Stat curStat = stats24.get(i);
                    Account player = dbUsers.getUser(curStat.getUserId());
                    int points = curStat.getPoints();

                %>
                <li><%=player.getUsername()%> - <%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</li>
                <%
                    }
                %>
            </ul>
        </div>


        <div class="section">
            <h2>Top Performers of All Time</h2>
            <ul>
                <%Collections.sort(stats, new Comparator<Stat>() {
                    public int compare(Stat s1, Stat s2) {
                        return Integer.compare(s2.getPoints(), s1.getPoints());
                    }
                });
                    for(int i = 0; i<some; i++){
                    Stat curStat = stats.get(i);
                    Account player = dbUsers.getUser(curStat.getUserId());
                    int points = curStat.getPoints();

                %>
                <li><%=player.getUsername()%> - <%=points%>/<%=maxs%>(<%=curStat.getPercent()%>)</li>
                <%
                    }
                %>
                <!-- etc. -->
            </ul>
        </div>

        <%
            Collections.sort(stats24, new Comparator<Stat>() {
                public int compare(Stat s1, Stat s2) {
                    return Integer.compare(s2.getPoints(), s1.getPoints());
                }
            });
        %>
        <div class="section">
            <h2>Top Performers in Last 24 Hours</h2>
            <ul>
                <%for(int i = 0; i<some24; i++){
                    Stat curStat = stats24.get(i);
                    Account player = dbUsers.getUser(curStat.getUserId());
                    int points = curStat.getPoints();

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
            <form method="get" action="QuizPageServlet">
                <input type="hidden" name="quizId" value="<%=id%>">
                <button type="submit" name="mode" value="multiple" class="start-quiz">Start quiz on multiple page</button>
                <button type="submit" name="mode" value="one" class="start-quiz">Start quiz on one page</button>
                <button type="submit" name="mode" value="practice" class="start-quiz">Start quiz on practice mode</button>
                <%
                    if(user.getId() == creator.getId() || user.isAdmin()){
                %>
                    <button type="submit" name="mode" value="delete" class="start-quiz">delete this quiz</button>
                <%}
                %>
            </form>
        </div>
    </div>
</body>
</html>
