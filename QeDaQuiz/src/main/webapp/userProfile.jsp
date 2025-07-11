<%@ page import="AccountManager.Account" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Daos.UsersDao" %>
<%@ page import="Daos.HistoryDao" %>
<%@ page import="quiz.history.History" %>
<%@ page import="java.util.Deque" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }
    Account profileUser = null;
    if(request.getParameter("userId") != null){
        UsersDao dbUsers = (UsersDao) request.getServletContext().getAttribute("accountDB");
        profileUser = dbUsers.getUser(Integer.parseInt(request.getParameter("userId")));
    }else{
        profileUser = (Account) request.getAttribute("profileUser");
    }
    if (profileUser == null || profileUser.getId() == currentUser.getId()) {
        response.sendRedirect("MainPageServlet");
        return;
    }
    HistoryDao histDao=(HistoryDao)request.getServletContext().getAttribute("histDao");
    UsersDao userDao=(UsersDao)request.getServletContext().getAttribute("accountDB");
    Integer quizzesTaken = userDao.getTakenQuizesQuantity(profileUser.getId());
    Integer quizzesMade = userDao.getMadeQuizesQuantity(profileUser.getId());
    List<String> achievements = userDao.getAchievements(profileUser.getId());
    History h = histDao.getUserCreatingHistory(profileUser.getId());
    Deque<Quiz> d = h.getQuizes();
    List<Quiz> createdQuizzes = new ArrayList<Quiz>(d);
    List<Quiz> recentCreatedQuizzes = new ArrayList<Quiz>();
    if(createdQuizzes.size() > 3){
        for(int i = 0; i<3; i++){
            recentCreatedQuizzes.add(createdQuizzes.get(i));
        }
        createdQuizzes = null;
    }
    if (quizzesTaken == null) quizzesTaken = 0;
    if (quizzesMade == null) quizzesMade = 0;
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
        <a href="historyPage.jsp?action=inbox&userId=<%= profileUser.getId() %>" class="history-btn">📜 History</a>
        <a href="MessageServlet?recipientId=<%=profileUser.getId()%>" class="nav-link send-message-btn">Send Message</a>
    </div>

    <!-- Profile Header -->
    <div class="profile-header">
        <div class="profile-picture-large">
            <img src="<%=profileUser.getPhoto()%>" alt="Profile Picture"/>
        </div>
        <div class="profile-info">
            <h1 class="username"><%=profileUser.getUsername()%></h1>
            <p class="profile-type">User Profile</p>
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
            This user hasn't earned any achievements yet.
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
            This user hasn't created any public quizzes yet.
        </p>
        <% } %>
    </div>

</div>
</body>
</html>