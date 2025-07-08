<%@ page import="AccountManager.Account" %>
<%@ page import="AccountManager.Message" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    List<Message> sentMessages = (List<Message>) request.getAttribute("sentMessages");
    String successMessage = (String) session.getAttribute("successMessage");
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
%>
<html>
<head>
    <title>Sent Messages - QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/message.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>
<div class="message-container">
    <!-- Navigation -->
    <div class="navigation">
        <a href="MainPageServlet" class="nav-link">← Back to Main Page</a>
        <a href="MessageServlet?action=inbox" class="nav-link">📥 Inbox</a>
        <a href="MessageServlet?action=sent" class="nav-link active">📤 Sent Messages</a>
    </div>

    <!-- Header -->
    <div class="message-header">
        <h1>📤 Sent Messages</h1>
        <p>Messages you've sent to other users</p>
    </div>

    <!-- Success Message -->
    <% if (successMessage != null) { %>
    <div class="alert alert-success">
        <%=successMessage%>
    </div>
    <% } %>

    <!-- Messages List -->
    <div class="messages-container">
        <% if (sentMessages != null && !sentMessages.isEmpty()) { %>
        <div class="messages-list">
            <% for (Message message : sentMessages) { %>
            <div class="message-item sent <%=message.getType().toLowerCase()%>">
                <div class="message-header-item">
                    <div class="recipient-info">
                        <img src="<%=message.getReceiver().getPhoto()%>" alt="Profile" class="recipient-avatar">
                        <div class="recipient-details">
                            <h3>To: <%=message.getReceiver().getUsername()%></h3>
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
                <h2>📭 No sent messages</h2>
                <p>You haven't sent any messages yet. Start a conversation with other users!</p>
                <a href="MainPageServlet" class="btn btn-primary">Go Back to Main Page</a>
            </div>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
