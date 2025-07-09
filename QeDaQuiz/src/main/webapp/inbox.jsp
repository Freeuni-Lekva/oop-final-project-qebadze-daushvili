<%@ page import="AccountManager.Account" %>
<%@ page import="AccountManager.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    List<Message> receivedMessages = (List<Message>) request.getAttribute("receivedMessages");
    String successMessage = (String) session.getAttribute("successMessage");
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }

    // Sort messages to show newest first (assuming messages have a timestamp or ID for ordering)
    if (receivedMessages != null) {
        Collections.reverse(receivedMessages);
    }
%>
<html>
<head>
    <title>Inbox - QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/message.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>
<div class="message-container">
    <!-- Navigation -->
    <div class="navigation">
        <a href="MainPageServlet" class="nav-link">← Back to Main Page</a>
        <a href="MessageServlet?action=inbox" class="nav-link active">📥 Inbox</a>
        <a href="MessageServlet?action=sent" class="nav-link">📤 Sent Messages</a>
    </div>

    <!-- Header -->
    <div class="message-header">
        <h1>📥 Your Inbox</h1>
        <p>Messages received from other users (newest first)</p>
    </div>

    <!-- Success Message -->
    <% if (successMessage != null) { %>
    <div class="alert alert-success">
        <%=successMessage%>
    </div>
    <% } %>

    <!-- Messages List -->
    <div class="messages-container">
        <% if (receivedMessages != null && !receivedMessages.isEmpty()) { %>
        <div class="messages-list">
            <% for (Message message : receivedMessages) { %>
            <div class="message-item <%=message.getType().toLowerCase()%>">
                <div class="message-header-item">
                    <div class="sender-info">
                        <img src="<%=message.getSender().getPhoto()%>" alt="Profile" class="sender-avatar">
                        <div class="sender-details">
                            <h3><%=message.getSender().getUsername()%></h3>
                            <span class="message-type">
                                <% if ("NOTE".equals(message.getType())) { %>
                                📝 Note
                                <% } else if ("FRIEND_REQUEST".equals(message.getType())) { %>
                                👥 Friend Request
                                <% } else if ("CHALLENGE".equals(message.getType())) { %>
                                🎯 Quiz Challenge
                                <% } else { %>
                                📧 <%=message.getType()%>
                                <% } %>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="message-content">
                    <p><%=message.getContent()%></p>
                </div>
            </div>
            <% } %>
        </div>
        <% } else { %>
        <div class="no-messages">
            <div class="empty-state">
                <h2>📭 No messages yet</h2>
                <p>You haven't received any messages yet. When someone sends you a message, it will appear here.</p>
                <a href="MainPageServlet" class="btn btn-primary">Go Back to Main Page</a>
            </div>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>