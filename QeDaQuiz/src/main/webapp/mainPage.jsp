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
    List<Message> sentMessages = (List<Message>) request.getAttribute("sentMessages");
    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
    History takenHistory = (History) request.getAttribute("takenHistory");
    History createdHistory = (History) request.getAttribute("createdHistory");
    List<Quiz> most_popular_quizzes=(List<Quiz>) request.getAttribute("mostPopularQuizzes");
    List<Account> searchResults = (List<Account>) request.getAttribute("searchResults");
    String searchQuery = (String) request.getAttribute("searchQuery");
%>
<html>
<head>
    <title>MainPage_QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>

<div class="mainpage-container">
    <!-- Profile Header -->
    <div class="user-info gradient">
        <div class="profile-pic">
            <img src="<%= user.getPhoto()%>" alt="Profile Picture"/>
        </div>
        <div class="details">
            <p><%=user.getUsername()%></p>
            <span>User Profile</span>
        </div>
        <div class="profile-actions">
            <a href="CreateQuizServlet" class="create-quiz-btn">
                <span class="btn-icon">📝</span>
                Create Quiz
            </a>
        </div>
    </div>

    <!-- Summary Boxes -->
    <div class="summary-wrapper">
        <div class="summary-box">
            <h3>📘 <%= takenHistory != null ? takenHistory.getSize() : 0 %></h3>
            <p>Quizzes Taken</p>
        </div>
        <div class="summary-box">
            <h3>📝 <%= createdHistory != null ? createdHistory.getSize() : 0 %></h3>
            <p>Quizzes Created</p>
        </div>
        <div class="summary-box">
            <h3>🏆 <%= achievements != null ? achievements.size() : 0 %></h3>
            <p>Achievements</p>
        </div>
        <div class="summary-box">
            <h3>📥 <%= receivedMessages != null ? receivedMessages.size() : 0 %></h3>
            <p>Received Messages</p>
        </div>
        <div class="summary-box">
            <h3>📤 <%= sentMessages != null ? sentMessages.size() : 0 %></h3>
            <p>Sent Messages</p>
        </div>
    </div>

    <!-- Search Users -->
    <div class="user-search">
        <h2>🔍 Search Users</h2>
        <form method="GET" action="MainPageServlet" class="search-form">
            <div class="search-container">
                <input type="text" name="search" placeholder="Search for users..."
                       value="<%= searchQuery != null ? searchQuery : "" %>" class="search-input">
                <button type="submit" class="search-button">Search</button>
                <% if (searchQuery != null && !searchQuery.trim().isEmpty()) { %>
                <a href="MainPageServlet" class="clear-search">Clear</a>
                <% } %>
            </div>
        </form>

        <% if (searchResults != null) { %>
        <div class="search-results">
            <% if (searchResults.size() > 0) { %>
            <h3>Search Results (<%=searchResults.size()%> found):</h3>
            <ul class="user-results">
                <% for (Account searchUser : searchResults) { %>
                <li class="user-result-item">
                    <div class="user-result">
                        <img src="<%=searchUser.getPhoto()%>" alt="Profile" class="search-profile-pic">
                        <div class="user-result-info">
                            <span class="username"><%=searchUser.getUsername()%></span>
                            <div class="user-actions">
                                <a href="UserProfileServlet?userId=<%=searchUser.getId()%>" class="view-profile">View Profile</a>
                                <a href="MessageServlet?recipientId=<%=searchUser.getId()%>" class="send-message">Send Message</a>
                            </div>
                        </div>
                    </div>
                </li>
                <% } %>
            </ul>
            <% } else { %>
            <div class="empty-state">
                <p>No users found matching "<%=searchQuery%>"</p>
            </div>
            <% } %>
        </div>
        <% } %>
    </div>

    <!-- Messages Navigation -->
    <div class="messages">
        <h2>💬 Messages</h2>
        <div class="message-navigation">
            <a href="MessageServlet?action=inbox" class="message-nav-btn inbox-btn">
                <span class="btn-icon">📥</span>
                <span class="btn-text">Inbox</span>
            </a>
            <a href="MessageServlet?action=sent" class="message-nav-btn sent-btn">
                <span class="btn-icon">📤</span>
                <span class="btn-text">Sent Messages</span>
            </a>
        </div>
    </div>

    <!-- Announcements -->
    <div class="announcements">
        <h2>📢 Announcements</h2>
        <% if(announcements != null && !announcements.isEmpty()) { %>
        <ul class="content-list">
            <% for(int i=0;i<Math.min(10, announcements.size());i++ ){ %>
            <li><%= announcements.get(i) %></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>No announcements available.</p>
        </div>
        <% } %>
    </div>

    <!-- Achievements -->
    <div class="achievements">
        <h2>🏆 Achievements</h2>
        <% if (achievements != null && achievements.size() > 0) { %>
        <ul class="content-list">
            <% for(int i=0;i<Math.min(10, achievements.size());i++) {
                String ach = achievements.get(i); %>
            <li><%= ach %></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>This user hasn't earned any achievements yet.</p>
        </div>
        <% } %>
    </div>

    <!-- Created Quizzes -->
    <div class="created-quizes">
        <h2>📝 Created Quizzes</h2>
        <% if (createdHistory != null && createdHistory.getSize() > 0) { %>
        <ul class="content-list">
            <% for(int i=Math.min(10, createdHistory.getSize());i>=1;i--) {
                Quiz quiz = createdHistory.getQuiz(i); %>
            <li><a href="quizPage.jsp?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>This user hasn't created any public quizzes yet.</p>
        </div>
        <% } %>
    </div>

    <!-- Recently Created Quizzes -->
    <div class="recent-quizes">
        <h2>📅 Recently created quizzes</h2>
        <% if (recentQuizzes != null && !recentQuizzes.isEmpty()) { %>
        <ul class="content-list">
            <% for(int i=0;i<Math.min(10, recentQuizzes.size());i++) {
                Quiz quiz=recentQuizzes.get(i); %>
            <li><a href="quizPage.jsp?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>No quizzes have been created recently.</p>
        </div>
        <% } %>
    </div>

    <!-- Most Popular Quizzes -->
    <div class="most-popular-quizes">
        <h2>📊 Most popular quizzes</h2>
        <% if (most_popular_quizzes != null && !most_popular_quizzes.isEmpty()) { %>
        <ul class="content-list">
            <% for(int i=0;i<Math.min(10, most_popular_quizzes.size());i++) {
                Quiz quiz=most_popular_quizzes.get(i); %>
            <li><a href="quizPage.jsp?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>No popular quizzes yet.</p>
        </div>
        <% } %>
    </div>

    <!-- Recently Taken Quizzes -->
    <div class="taken-quizes">
        <h2>📈 Recently taken quizzes</h2>
        <% if (takenHistory != null && takenHistory.getSize() > 0) { %>
        <ul class="content-list">
            <% for(int i=Math.min(10, takenHistory.getSize());i>=1;i--) {
                Quiz quiz = takenHistory.getQuiz(i); %>
            <li><a href="quizPage.jsp?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>You haven't taken any quizzes yet.</p>
        </div>
        <% } %>
    </div>
</div>
<a href="/allQuizes.jsp">Create new account</a>
</body>
</html>