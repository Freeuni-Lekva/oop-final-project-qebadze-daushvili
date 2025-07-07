<%@ page import="AccountManager.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Account currentUser = (Account) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    Account recipient = (Account) request.getAttribute("recipient");
    if (recipient == null) {
        response.sendRedirect("MainPageServlet");
        return;
    }

    String friendStatus = (String) request.getAttribute("friendStatus");
    String error = (String) request.getAttribute("error");
    String successMessage = (String) session.getAttribute("successMessage");
    if (successMessage != null) {
        session.removeAttribute("successMessage");
    }
%>
<html>
<head>
    <title>Send Message to <%=recipient.getUsername()%> - QeDa</title>
    <link rel="stylesheet" type="text/css" href="/css/message.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
</head>
<body>
<div class="message-container">
    <!-- Navigation -->
    <div class="navigation">
        <a href="MainPageServlet" class="nav-link">← Back to Main Page</a>
        <a href="MessageServlet?action=inbox" class="nav-link">📥 Inbox</a>
        <a href="MessageServlet?action=sent" class="nav-link">📤 Sent Messages</a>
        <a href="UserProfileServlet?userId=<%=recipient.getId()%>" class="nav-link">👤 View Profile</a>
    </div>

    <!-- Header -->
    <div class="message-header">
        <h1>Send Message</h1>
        <div class="recipient-info">
            <img src="<%=recipient.getPhoto()%>" alt="Profile Picture" class="recipient-avatar">
            <div class="recipient-details">
                <h2>To: <%=recipient.getUsername()%></h2>
                <p class="friend-status">
                    <% if ("ACCEPTED".equals(friendStatus)) { %>
                    <span class="status-friends">✅ You are friends</span>
                    <% } else if ("PENDING".equals(friendStatus)) { %>
                    <span class="status-pending">⏳ Friend request pending</span>
                    <% } else { %>
                    <span class="status-not-friends">➕ Not friends yet</span>
                    <% } %>
                </p>
            </div>
        </div>
    </div>

    <!-- Success/Error Messages -->
    <% if (successMessage != null) { %>
    <div class="alert alert-success">
        <%=successMessage%>
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="alert alert-error">
        <%=error%>
    </div>
    <% } %>

    <!-- Message Form -->
    <div class="message-form-container">
        <form method="POST" action="MessageServlet" class="message-form">
            <input type="hidden" name="recipientId" value="<%=recipient.getId()%>">

            <!-- Message Type Selection -->
            <div class="form-group">
                <label for="messageType">Message Type:</label>
                <select name="messageType" id="messageType" class="form-select" required onchange="toggleQuizField()">
                    <option value="">Select message type...</option>
                    <option value="NOTE">📝 Regular Note</option>
                    <% if (!"ACCEPTED".equals(friendStatus)) { %>
                    <option value="FRIEND_REQUEST">👥 Friend Request</option>
                    <% } %>
                    <option value="CHALLENGE">🎯 Quiz Challenge</option>
                </select>
            </div>

            <!-- Quiz ID field (only for challenges) -->
            <div class="form-group" id="quizIdGroup" style="display: none;">
                <label for="quizId">Quiz ID for Challenge:</label>
                <input type="number" name="quizId" id="quizId" class="form-input" min="1"
                       placeholder="Enter the quiz ID you want to challenge with">
                <small class="form-help">You can find quiz IDs on the quiz pages or in your created quizzes list.</small>
            </div>

            <!-- Message Content -->
            <div class="form-group">
                <label for="messageContent">Message:</label>
                <textarea name="messageContent" id="messageContent" class="form-textarea"
                          rows="6" required placeholder="Write your message here..."></textarea>
                <small class="form-help">Maximum 500 characters</small>
            </div>

            <!-- Buttons -->
            <div class="form-buttons">
                <button type="submit" class="btn btn-primary">Send Message</button>
                <button type="button" class="btn btn-secondary" onclick="history.back()">Cancel</button>
            </div>
        </form>
    </div>

    <!-- Message Type Information -->
    <div class="message-info">
        <h3>Message Types:</h3>
        <div class="info-grid">
            <div class="info-card">
                <h4>📝 Regular Note</h4>
                <p>Send a casual message or note to another user. Perfect for general conversation.</p>
            </div>
            <% if (!"ACCEPTED".equals(friendStatus)) { %>
            <div class="info-card">
                <h4>👥 Friend Request</h4>
                <p>Send a friend request along with a personal message. Once accepted, you'll be friends!</p>
            </div>
            <% } %>
            <div class="info-card">
                <h4>🎯 Quiz Challenge</h4>
                <p>Challenge this user to take a specific quiz. Include the quiz ID and your challenge message.</p>
            </div>
        </div>
    </div>
</div>

<script>
    function toggleQuizField() {
        const messageType = document.getElementById('messageType').value;
        const quizIdGroup = document.getElementById('quizIdGroup');
        const quizIdInput = document.getElementById('quizId');

        if (messageType === 'CHALLENGE') {
            quizIdGroup.style.display = 'block';
            quizIdInput.required = true;
        } else {
            quizIdGroup.style.display = 'none';
            quizIdInput.required = false;
            quizIdInput.value = '';
        }
    }

    // Character limit for message content
    document.getElementById('messageContent').addEventListener('input', function() {
        if (this.value.length > 500) {
            this.value = this.value.substring(0, 500);
        }
    });
</script>
</body>
</html>