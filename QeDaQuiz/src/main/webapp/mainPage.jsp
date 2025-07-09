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
    List<Account> requests=(List<Account>) request.getAttribute("requests");
    List<Account> friends=(List<Account>) request.getAttribute("friends");
    List<Account> searchResults = (List<Account>) request.getAttribute("searchResults");
    String searchQuery = (String) request.getAttribute("searchQuery");
%>
<html>
<head>
    <title>MainPage_QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<div class="page-wrapper">
    <div class="left-panel">
        <!-- Messages -->
        <div class="messages">
            <h2>💬 Your MessageBox</h2>
            <div class="inbox-and-sent">
                <a href="MessageServlet?action=inbox" class="msg-button">📥 Inbox</a>
                <a href="MessageServlet?action=sent" class="msg-button">📤 Sent Messages</a>
            </div>
        </div>

        <div class="friends">
            <h2>👥 Your Friends</h2>
            <% if (friends != null && !friends.isEmpty()) { %>
            <ul class="friend-list">
                <% for (Account friend : friends) { %>
                <li class="friend-item">
                    <div class="friend-info">
                        <img src="<%= friend.getPhoto() %>" alt="Friend" class="friend-pic"/>
                        <a href="UserProfileServlet?userId=<%= friend.getId() %>" class="friend-name"><%= friend.getUsername() %></a>
                    </div>
                    <div class="friend-actions">
                        <a href="MessageServlet?recipientId=<%= friend.getId() %>" class="small-btn">✉️</a>
                        <form method="POST" action="MainPageServlet" style="display: inline;">
                            <input type="hidden" name="userId" value="<%= friend.getId() %>" />
                            <button type="submit" name="action" value="remove" class="small-btn danger-btn">❌</button>
                        </form>
                    </div>
                </li>
                <% } %>
            </ul>
            <% } else { %>
            <div class="empty-state">No friends yet.</div>
            <% } %>
        </div>
    </div>

    <div class="mainpage-container">
        <div class="main-content">
            <!-- Navigation Header -->
            <div class="navigation">
                <a href="index.jsp" class="log-out-button">&larr; Log Out</a>
                <% if (requests.size() > 0) { %>
                <span class="friend-requests">
                    🔔 You have <%= requests.size() %> friend request<%= requests.size() == 1 ? "" : "s" %>
                </span>
                <% } %>
            </div>

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
                    <a href="AllQuizServlet" class="all-quiz-btn">
                        <span class="btn-icon">📘</span>
                        All Quizzes
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
                                        <% if (friends.contains(searchUser)) { %>
                                        <form method="POST" action="MainPageServlet" class="inline-form">
                                            <input type="hidden" name="userId" value="<%=searchUser.getId()%>" />
                                            <button type="submit" name="action" value="remove" class="remove-friend-btn">Remove Friend</button>
                                        </form>
                                        <% } else { %>
                                        <form method="POST" action="MainPageServlet" class="inline-form">
                                            <input type="hidden" name="userId" value="<%=searchUser.getId()%>" />
                                            <button type="submit" name="action" value="sendRequest" class="add-friend-btn">Add Friend</button>
                                        </form>
                                        <% } %>
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
    </div>

    <!-- Friend requests panel - only show if there are requests -->
    <% if (!requests.isEmpty()) { %>
    <div class="friend-requests-panel">
        <h3>🔔 Friend Requests</h3>
        <ul class="friend-request-list">
            <% for(Account requester : requests) { %>
            <li class="friend-request-item">
                <div class="friend-request-header">
                    <img src="<%= requester.getPhoto() %>" alt="Profile" class="friend-request-pic"/>
                    <span><%= requester.getUsername() %></span>
                </div>
                <form method="POST" action="MainPageServlet">
                    <input type="hidden" name="userId" value="<%= requester.getId() %>" />
                    <button type="submit" name="action" value="accept" class="accept-btn">Accept</button>
                    <button type="submit" name="action" value="reject" class="reject-btn">Reject</button>
                </form>
            </li>
            <% } %>
        </ul>
    </div>
    <% } %>
</div>

</body>
</html>