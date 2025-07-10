<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="AccountManager.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser=(Account) session.getAttribute("user");
    List<String> announcements = (List<String>) request.getAttribute("announcements");
    Integer total_users = (Integer) request.getAttribute("total_users");
    Integer total_quizes_taken = (Integer) request.getAttribute("total_quizes_taken");
%>
<html>
<head>
    <title>AdminPage_QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/adminPage.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="page-wrapper">

    <!-- Back Button -->
    <div class="back-button-container">
        <a href="MainPageServlet" class="back-button">← Back to Main Page</a>
    </div>

    <!-- statistics -->
    <div class="summary-wrapper">
        <div class="summary-box">
            <h3>👥 <%= total_users != null ? total_users : 0 %></h3>
            <p>Total Users</p>
        </div>
        <div class="summary-box">
            <h3>📘 <%= total_quizes_taken != null ? total_quizes_taken : 0 %></h3>
            <p>Quizzes Taken</p>
        </div>
    </div>

    <!-- Add Announcement Form -->
    <div class="add-announcement">
        <h2>📝 Make Announcement</h2>
        <form method="post" action="AdminPageServlet" class="announcement-form">
            <input type="hidden" name="action" value="addAnnouncement"/>
            <div class="form-group">
                <textarea name="announcementText" placeholder="Enter your announcement here..." class="announcement-input" rows="3" required></textarea>
            </div>
            <button type="submit" class="add-announcement-btn">📢 Add Announcement</button>
        </form>
    </div>

    <!-- announcements -->
    <div class="announcements">
        <h2>📢 Announcements</h2>
        <% if (announcements != null && !announcements.isEmpty()) { %>
        <ul class="content-list">
            <% for (int i = 0; i < Math.min(10, announcements.size()); i++) { %>
            <li><%= announcements.get(i) %></li>
            <% } %>
        </ul>
        <% } else { %>
        <div class="empty-state">
            <p>No announcements available.</p>
        </div>
        <% } %>
    </div>

    <!-- users -->
    <div class="user-management">
        <h2>🔍 Search Users</h2>

        <!-- Search Form -->
        <form method="get" action="AdminPageServlet" class="user-search-form">
            <input type="text" name="searchQuery" placeholder="Search for users..." class="search-input"/>
            <button type="submit" class="search-button">Search</button>
        </form>

        <!-- Users List -->
        <div class="user-list">
            <%
                ArrayList<Account> users = (ArrayList<AccountManager.Account>) request.getAttribute("users");
                if (users != null && !users.isEmpty()) {
                    for (AccountManager.Account user : users) {
                        if(user.getId()==currentUser.getId())continue;
            %>
            <div class="user-card">
                <div class="user-info">
                    <a href="UserProfileServlet?userId=<%= user.getId() %>" class="user-link">
                        <img src="<%= user.getPhoto() %>" alt="Profile" class="user-avatar"/>
                        <span><%= user.getUsername() %></span>
                    </a>
                    <% if (user.isAdmin()) { %>
                    <span class="admin-tag">Admin</span>
                    <% } %>
                </div>
                <%if(!user.isAdmin()) {%>
                <div class="user-actions">
                    <form method="post" action="AdminPageServlet" style="display:inline;">
                        <input type="hidden" name="action" value="removeUser"/>
                        <input type="hidden" name="userId" value="<%= user.getId() %>"/>
                        <button type="submit" class="remove-btn">🗑 Remove Account</button>
                    </form>
                    <% if (!user.isAdmin()) { %>
                    <form method="post" action="AdminPageServlet" style="display:inline;">
                        <input type="hidden" name="action" value="makeAdmin"/>
                        <input type="hidden" name="userId" value="<%= user.getId() %>"/>
                        <button type="submit" class="make-admin-btn">⭐ Make Admin</button>
                    </form>
                    <% } %>
                </div>
                <%}%>
            </div>
            <%
                }
            } else {
            %>
            <p class="no-users">No users found.</p>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
