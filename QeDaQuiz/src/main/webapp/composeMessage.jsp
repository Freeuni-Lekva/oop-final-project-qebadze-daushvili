<%@ page import="AccountManager.Account" %>
<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="java.util.ArrayList" %>
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

    ArrayList<Quiz> availableQuizzes = (ArrayList<Quiz>) request.getAttribute("availableQuizzes");
    if (availableQuizzes == null) {
        availableQuizzes = new ArrayList<Quiz>();
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
    <link rel="stylesheet" type="text/css" href="/css/messages.css">
    <link rel="stylesheet" type="text/css" href="/css/mainPage.css">
    <style>
        .quiz-search-container {
            margin-bottom: 10px;
        }

        .quiz-search-input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 5px;
        }

        .quiz-option {
            padding: 8px;
            border-bottom: 1px solid #eee;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .quiz-option:hover {
            background-color: #f5f5f5;
        }

        .quiz-option.selected {
            background-color: #e3f2fd;
            border-left: 3px solid #2196f3;
        }

        .quiz-info {
            font-size: 12px;
            color: #666;
            margin-top: 2px;
        }

        .quiz-list {
            max-height: 200px;
            overflow-y: auto;
            border: 1px solid #ddd;
            border-radius: 4px;
            display: none;
        }

        .no-quizzes {
            padding: 10px;
            text-align: center;
            color: #666;
            font-style: italic;
        }
    </style>
</head>
<body>
<div class="message-container">
    <!-- Navigation -->
    <div class="navigation">
        <a href="MainPageServlet" class="nav-link">← Back to Main Page</a>
        <a href="MessageServlet?action=inbox" class="nav-link">📥 Inbox</a>
        <a href="MessageServlet?action=sent" class="nav-link">📤 Sent Messages</a>
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
            <input type="hidden" name="selectedQuizId" id="selectedQuizId" value="">

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

            <!-- Quiz Selection (only for challenges) -->
            <div class="form-group" id="quizSelectionGroup" style="display: none;">
                <label for="quizSearch">Choose a Quiz for Challenge:</label>
                <div class="quiz-search-container">
                    <input type="text" id="quizSearch" class="quiz-search-input"
                           placeholder="Search for a quiz by name..."
                           onkeyup="filterQuizzes()"
                           onfocus="showQuizList()"
                           readonly>
                    <div id="quizList" class="quiz-list">
                        <% if (availableQuizzes.isEmpty()) { %>
                        <div class="no-quizzes">No quizzes available</div>
                        <% } else { %>
                        <% for (Quiz quiz : availableQuizzes) { %>
                        <div class="quiz-option"
                             data-quiz-id="<%=quiz.getQuizId()%>"
                             data-quiz-name="<%=quiz.getQuizName()%>"
                             onclick="selectQuiz(<%=quiz.getQuizId()%>, '<%=quiz.getQuizName().replace("'", "\\'")%>')">
                            <div class="quiz-name"><%=quiz.getQuizName()%></div>
                            <div class="quiz-info"><%=quiz.getQuizDescription()%></div>
                        </div>s
                        <% } %>
                        <% } %>
                    </div>
                </div>
                <small class="form-help">Search and select a quiz to challenge this user with.</small>
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
                <p>Challenge this user to take a specific quiz. Search and select from available quizzes.</p>
            </div>
        </div>
    </div>
</div>

<script>
    let selectedQuizId = null;
    let allQuizzes = [];

    // Initialize quiz data
    <% if (!availableQuizzes.isEmpty()) { %>
    allQuizzes = [
        <% for (int i = 0; i < availableQuizzes.size(); i++) {
            Quiz quiz = availableQuizzes.get(i);
        %>
        {
            id: <%=quiz.getQuizId()%>,
            name: "<%=quiz.getQuizName().replace("\"", "\\\"")%>",
            description: "<%=quiz.getQuizDescription().replace("\"", "\\\"")%>"
        }<%= i < availableQuizzes.size() - 1 ? "," : "" %>
        <% } %>
    ];
    <% } %>

    function toggleQuizField() {
        const messageType = document.getElementById('messageType').value;
        const quizSelectionGroup = document.getElementById('quizSelectionGroup');
        const quizSearch = document.getElementById('quizSearch');

        if (messageType === 'CHALLENGE') {
            quizSelectionGroup.style.display = 'block';
            quizSearch.required = true;
            quizSearch.removeAttribute('readonly');
        } else {
            quizSelectionGroup.style.display = 'none';
            quizSearch.required = false;
            quizSearch.setAttribute('readonly', 'readonly');
            selectedQuizId = null;
            document.getElementById('selectedQuizId').value = '';
            document.getElementById('quizSearch').value = '';
            hideQuizList();
        }
    }

    function showQuizList() {
        if (document.getElementById('messageType').value === 'CHALLENGE') {
            document.getElementById('quizList').style.display = 'block';
        }
    }

    function hideQuizList() {
        document.getElementById('quizList').style.display = 'none';
    }

    function selectQuiz(quizId, quizName) {
        selectedQuizId = quizId;
        document.getElementById('selectedQuizId').value = quizId;
        document.getElementById('quizSearch').value = quizName;

        // Update visual selection
        const options = document.querySelectorAll('.quiz-option');
        options.forEach(option => option.classList.remove('selected'));

        const selectedOption = document.querySelector(`[data-quiz-id="${quizId}"]`);
        if (selectedOption) {
            selectedOption.classList.add('selected');
        }

        hideQuizList();
    }

    function filterQuizzes() {
        const searchTerm = document.getElementById('quizSearch').value.toLowerCase();
        const options = document.querySelectorAll('.quiz-option');

        options.forEach(option => {
            const quizName = option.getAttribute('data-quiz-name').toLowerCase();
            if (quizName.includes(searchTerm)) {
                option.style.display = 'block';
            } else {
                option.style.display = 'none';
            }
        });

        showQuizList();
    }

    // Close quiz list when clicking outside
    document.addEventListener('click', function(event) {
        const quizContainer = document.querySelector('.quiz-search-container');
        const quizList = document.getElementById('quizList');

        if (!quizContainer.contains(event.target)) {
            hideQuizList();
        }
    });

    // Character limit for message content
    document.getElementById('messageContent').addEventListener('input', function() {
        if (this.value.length > 500) {
            this.value = this.value.substring(0, 500);
        }
    });

    // Form validation
    document.querySelector('.message-form').addEventListener('submit', function(e) {
        const messageType = document.getElementById('messageType').value;

        if (messageType === 'CHALLENGE' && !selectedQuizId) {
            e.preventDefault();
            alert('Please select a quiz for the challenge.');
            return false;
        }

        return true;
    });
</script>
</body>
</html>