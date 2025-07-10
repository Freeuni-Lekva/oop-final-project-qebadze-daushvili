<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.questions.Question" %>
<%@ page import="quiz.questions.MultipleChoiceQuestion" %>
<%@ page import="java.util.List" %>
<%@ page import="Constantas.Constantas" %>
<%@ page import="Daos.QuizDao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  int quizId = 0;
  if(request.getParameter("quizId") != null) {
    quizId = Integer.parseInt(request.getParameter("quizId"));
  }else{
    quizId = (Integer) session.getAttribute("quizId");
  }
  session.setAttribute("quizId", quizId);
  QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
  Quiz quiz = db.getQuiz(quizId);
  if (quiz == null) {
    response.sendRedirect("mainPage.jsp?error=No quiz selected");
    return;
  }
  List<Question> questions = quiz.getQuestions();
%>

<html>
<head>
  <title>Take Quiz</title>
  <link rel="stylesheet" type="text/css" href="/css/takeQuiz.css?v=247">
  <script>
    function validateForm() {
      let form = document.getElementById('quizForm');
      let questions = form.querySelectorAll('.question');
      let unanswered = [];
      questions.forEach(function(question, index) {
        let inputs = question.querySelectorAll('input[type="radio"], input[type="text"]');
        let hasAnswer = false;
        inputs.forEach(function(input) {
          if (input.type === 'radio' && input.checked) {
            hasAnswer = true;
          } else if (input.type === 'text' && input.value.trim() !== '') {
            hasAnswer = true;
          }
        });
        if (!hasAnswer) {
          unanswered.push(index + 1);
        }
      });
      if (unanswered.length > 0) {
        let message = 'Please answer the following questions: ' + unanswered.join(', ');
        alert(message);
        return false;
      }
    }
  </script>
</head>
<body>

<div class="quiz-container">
  <div style="margin-bottom: 20px;">
    <form action="MainPageServlet" method="get">
      <div class="submit-section">
        <button type="submit" class="submit-btn">🔙 Main Page</button>
      </div>
    </form>
  </div>
  <div class="quiz-header">
    <h1><%= quiz.getQuizName() %></h1>
    <p class="quiz-description"><%= quiz.getQuizDescription() %></p>
    <div class="quiz-info">
      <span class="question-count">Questions: <%= questions.size() %></span>
    </div>
  </div>

  <form id="quizForm" method="POST" action="TakeQuizInOnePageServlet" onsubmit="return validateForm()">
    <div class="questions-container">
      <% for (int i = 0; i < questions.size(); i++) {
      Question question = questions.get(i);
      String questionType = question.getType();
    %>

      <div class="question" id="question_<%= i %>">
        <div class="question-header">
          <span class="question-number">Question <%= i + 1 %></span>
          <span class="question-type"><%= questionType %></span>
        </div>

        <% if (questionType.equals(Constantas.MULTIPLE_CHOICE)) {
          MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
          List<String> answers = mcq.get_possible_answers();
        %>
        <div class="question-text">
          <p><%= question.getPrompt() %></p>
        </div>
        <div class="answer-options">
          <% for (int j = 0; j < answers.size(); j++) {
            String answer = answers.get(j);
          %>
          <label class="option">
            <input type="radio" name="question_<%= i %>" value="<%= answer %>" required>
            <span class="option-text"><%= answer %></span>
          </label>
          <% } %>
        </div>

        <% } else if (questionType.equals(Constantas.QUESTION_RESPONSE)) { %>
        <div class="question-text">
          <p><%= question.getPrompt() %></p>
        </div>
        <div class="answer-input">
          <input type="text" name="question_<%= i %>" placeholder="" required>
        </div>

        <% } else if (questionType.equals(Constantas.PICTURE_RESPONSE)) { %>
        <div class="picture-container">
          <img src="<%= question.getPrompt() %>">
        </div>
        <div class="answer-input">
          <input type="text" name="question_<%= i %>" placeholder="" required>
        </div>

        <% } else if (questionType.equals(Constantas.FILL_IN_THE_BLANK)) { %>
        <div class="fill-in-blank">
          <%
            String questionPrompt = question.getPrompt();
            int blankIndex = questionPrompt.indexOf("_");
            if (blankIndex != -1) {
              String start = questionPrompt.substring(0, blankIndex);
              String end = questionPrompt.substring(blankIndex + 1);
          %>
          <div class="answer-option">
            <%= start %>
            <input type="text" name="question_<%= i %>" placeholder="" required>
            <%= end %>
          </div>
          <% } else { %>
          <div class="questionText"><%= questionPrompt %></div>
          <div class="answer-option">
            <input type="text" name="question_<%= i %>" placeholder="" required>
          </div>
          <% } %>
        </div>
        <% } %>
      </div>

      <% } %>
    </div>

    <div class="submit-section">
      <button type="submit" class="submit-btn">Submit Quiz</button>
    </div>
  </form>
</div>

</body>
</html>