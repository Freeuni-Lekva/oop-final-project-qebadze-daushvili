<%@ page import="AccountManager.Account" %>
<%@ page import="Daos.CommunicationDao" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="AccountManager.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.history.History" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account user = (Account) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    List<String> announcements = (List<String>) request.getAttribute("announcements");
    List<String> achievements = (List<String>) request.getAttribute("achievements");
    List<Message> receivedMessages = (List<Message>) request.getAttribute("receivedMessages");
    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
    History takenHistory = (History) request.getAttribute("takenHistory");
    History createdHistory = (History) request.getAttribute("createdHistory");
    List<Quiz> most_popular_quizzes=(List<Quiz>) request.getAttribute("mostPopularQuizzes");

%>
<html>
<head>
    <title>MainPage_QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>
    <div class="mainpage-container">
        <div class="user-info">
            <div class="profile-pic">
                <img src="<%= user.getPhoto()%>"/>
            </div>
            <div class="details">
                <P><%=user.getUsername()%></P>
            </div>
        </div>
        <div class="announcements">
            <h2> Announcements! </h2>
            <ul>
                <% if(announcements!=null){%>
                <% for(int i=0;i<Math.min(10, announcements.size());i++ ){%>
                    <% String ann=announcements.get(i);%>
                    <li> <%= ann%></li>
                <%}}%>
            </ul>
        </div>
        <div class="recent-quizes">
            <h2> Recently created quizzes:</h2>
            <ul>
                <% if(recentQuizzes!=null){%>
                <% for(int i=0;i<Math.min(10, recentQuizzes.size());i++){%>
                <%Quiz quiz=recentQuizzes.get(i);%>
                <li><a href="QuizServlet?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
                <%}}%>
            </ul>
        </div>
        <div class="most-popular-quizes">
            <h2> Most popular quizzes:</h2>
            <ul>
                <% if(most_popular_quizzes!=null){%>
                <% for(int i=0;i<Math.min(10, most_popular_quizzes.size());i++){%>
                <%Quiz quiz=most_popular_quizzes.get(i);%>
                <li><a href="QuizServlet?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
                <%}}%>
            </ul>
        </div>
        <div class="taken-quizes">
            <h2> Recently taken quizzes:</h2>
            <ul>
                <% if(takenHistory!=null){%>
                <% for(int i=Math.min(10, takenHistory.getSize());i>=1;i--){%>
                <%Quiz quiz=takenHistory.getQuiz(i);%>
                <li><a href="QuizServlet?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
                <%}}%>
            </ul>
        </div>
        <div class="created-quizes">
            <h2> Recently created quizzes by you:</h2>
            <ul>
                <% if(createdHistory!=null){%>
                <% for(int i=Math.min(10, createdHistory.getSize());i>=1;i--){%>
                <%Quiz quiz=createdHistory.getQuiz(i);%>
                <li><a href="QuizServlet?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
                <%}}%>
            </ul>
        </div>
        <div class="achievements">
            <h2> Your achievements:</h2>
            <ul>
                <% if(achievements!=null){%>
                <% for(int i=0;i<Math.min(10, achievements.size());i++){%>
                <%String ach=achievements.get(i);%>
                <li><%=ach%></li>
                <%}}%>
            </ul>
        </div>

        <div class="messages">
            <h2> Your Messages:</h2>
            <ul>
                <% if(receivedMessages!=null){%>
                <% for(int i=0;i<Math.min(10, receivedMessages.size());i++){%>
                <%Message mes=receivedMessages.get(i);%>
                <li>Message from: <%= mes.getSender().getUsername()%>, <%=mes.getType()%></li>
                <%}}%>
            </ul>
        </div>

    </div>
</body>
</html>
