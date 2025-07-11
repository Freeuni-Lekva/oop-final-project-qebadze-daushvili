<%@ page import="AccountManager.Account" %>
<%@ page import="AccountManager.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    List<Message> receivedMessages = (List<Message>) request.getAttribute("receivedMessages");
    Collections.reverse(receivedMessages);
    String successMessage = (String) session.getAttribute("successMessage");
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
%>
<html>
<head>
    <title>Inbox - QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/messages.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
    <style>
        .message-content {
            max-height: 150px;
            overflow-y: auto;
            word-wrap: break-word;
            word-break: break-word;
            line-height: 1.4;
        }

        .message-content p {
            margin: 0;
            padding: 0;
            white-space: pre-wrap;
        }

        .message-item {
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            background-color: #f9f9f9;
        }

        .message-content::-webkit-scrollbar {
            width: 6px;
        }

        .message-content::-webkit-scrollbar-track {
            background: #f1f1f1;
            border-radius: 3px;
        }

        .message-content::-webkit-scrollbar-thumb {
            background: #888;
            border-radius: 3px;
        }

        .message-content::-webkit-scrollbar-thumb:hover {
            background: #555;
        }

        .quiz-challenge-actions {
            margin-top: 10px;
            padding-top: 10px;
            border-top: 1px solid #eee;
        }

        .btn {
            display: inline-block;
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .btn:hover {
            background-color: #0056b3;
        }

        .message-type {
            font-size: 0.9em;
            color: #666;
        }

        .sender-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .sender-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }

        .sender-details h3 {
            margin: 0;
            font-size: 1.1em;
        }
    </style>
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
        <p>Messages received from other users</p>
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
                    <% if ("CHALLENGE".equals(message.getType())) { %>
                    <div class="quiz-challenge-actions">
                        <a href="quizPage.jsp?id=<%=message.getQuizId()%>" class="btn btn-primary">Take Quiz Challenge</a>
                    </div>
                    <% } %>
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