<%@ page import="AccountManager.Account" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    Account profileUser = (Account) request.getAttribute("profileUser");
    if (profileUser == null) {
        response.sendRedirect("MainPageServlet");
        return;
    }

    Integer quizzesTaken = (Integer) request.getAttribute("quizzesTaken");
    Integer quizzesMade = (Integer) request.getAttribute("quizzesMade");
    List<String> achievements = (List<String>) request.getAttribute("achievements");
    List<Quiz> createdQuizzes = (List<Quiz>) request.getAttribute("createdQuizzes");
    List<Quiz> recentCreatedQuizzes = (List<Quiz>) request.getAttribute("recentCreatedQuizzes");
    Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");

    if (quizzesTaken == null) quizzesTaken = 0;
    if (quizzesMade == null) quizzesMade = 0;
    if (isOwnProfile == null) isOwnProfile = false;
%>
<html>
<head>
    <title><%=profileUser.getUsername()%>'s Profile - QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/userProfile.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>
<div class="profile-container">
    <!-- Navigation Bar -->
    <div class="navigation">
        <a href="MainPageServlet" class="nav-link">← Back to Main Page</a>
        <% if (!isOwnProfile) { %>
        <a href="MessageServlet?recipientId=<%=profileUser.getId()%>" class="nav-link send-message-btn">Send Message</a>
        <% } %>
    </div>

    <!-- Profile Header -->
    <div class="profile-header">
        <div class="profile-picture-large">
            <img src="<%=profileUser.getPhoto()%>" alt="Profile Picture"/>
        </div>
        <div class="profile-info">
            <h1 class="username"><%=profileUser.getUsername()%></h1>
            <% if (isOwnProfile) { %>
            <p class="profile-type">Your Profile</p>
            <% } else { %>
            <p class="profile-type">User Profile</p>
            <% } %>
        </div>
    </div>

    <!-- Statistics Section -->
    <div class="statistics-grid">
        <div class="stat-card">
            <div class="stat-number"><%=quizzesTaken%></div>
            <div class="stat-label">Quizzes Taken</div>
        </div>
        <div class="stat-card">
            <div class="stat-number"><%=quizzesMade%></div>
            <div class="stat-label">Quizzes Created</div>
        </div>
        <div class="stat-card">
            <div class="stat-number"><%=achievements != null ? achievements.size() : 0%></div>
            <div class="stat-label">Achievements</div>
        </div>
    </div>

    <!-- Achievements Section -->
    <div class="profile-section achievements-section">
        <h2>🏆 Achievements</h2>
        <% if (achievements != null && achievements.size() > 0) { %>
        <div class="achievements-grid">
            <% for (String achievement : achievements) { %>
            <div class="achievement-card">
                <div class="achievement-icon">🏅</div>
                <div class="achievement-name"><%=achievement%></div>
            </div>
            <% } %>
        </div>
        <% } else { %>
        <p class="no-content">
            <% if (isOwnProfile) { %>
            You haven't earned any achievements yet. Keep taking and creating quizzes!
            <% } else { %>
            This user hasn't earned any achievements yet.
            <% } %>
        </p>
        <% } %>
    </div>

    <!-- Created Quizzes Section -->
    <div class="profile-section created-quizzes-section">
        <h2>📝 Created Quizzes</h2>
        <%
            List<Quiz> quizzesToShow = createdQuizzes;
            if (quizzesToShow == null || quizzesToShow.size() == 0) {
                quizzesToShow = recentCreatedQuizzes;
            }
        %>
        <% if (quizzesToShow != null && quizzesToShow.size() > 0) { %>
        <div class="quizzes-grid">
            <% for (Quiz quiz : quizzesToShow) { %>
            <div class="quiz-card">
                <div class="quiz-info">
                    <h3 class="quiz-title">
                        <a href="QuizServlet?id=<%=quiz.getQuizId()%>"><%=quiz.getQuizName()%></a>
                    </h3>
                    <div class="quiz-stats">
                        <% if (quiz.getTotalScore() > 0) { %>
                        <span class="quiz-stat">🏆 Best: <%=quiz.getTotalScore()%></span>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <% } else { %>
        <p class="no-content">
            <% if (isOwnProfile) { %>
            You haven't created any quizzes yet. <a href="CreateQuizServlet">Create your first quiz!</a>
            <% } else { %>
            This user hasn't created any public quizzes yet.
            <% } %>
        </p>
        <% } %>
    </div>

</div>
</body>
</html>