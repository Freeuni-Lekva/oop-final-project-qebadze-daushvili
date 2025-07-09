<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.questions.Question" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="Constantas.Constantas" %>
<%@ page import="quiz.questions.MultipleChoiceQuestion" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Take Quiz</title>

</head>
<body>

<%
  int quizId = 0;
  if(request.getParameter("quizId") != null) {
    quizId = Integer.parseInt(request.getParameter("quizId"));
    session.setAttribute("questionNumber", null);
    session.setAttribute("showingFeedback", null);
  }else{
    quizId = (Integer) session.getAttribute("quizId");
  }
  session.setAttribute("quizId", quizId);
  QuizDao db = (QuizDao) request.getServletContext().getAttribute("quizDao");
  List<Question> questions;
  try {
    questions = db.getQuizQuestions(quizId);
  } catch (SQLException e) {
    throw new RuntimeException(e);
  }
  Integer questionNumber = (Integer) session.getAttribute("questionNumber");
  if (questionNumber == null) {
    questionNumber = 1;
    session.setAttribute("questionNumber", questionNumber);
  }
  int totalQuestions = questions.size();
  int progressPercentage = (int) ((double) questionNumber / totalQuestions * 100);

  Boolean showingFeedback = (Boolean) session.getAttribute("showingFeedback");
  if (showingFeedback == null) {
    showingFeedback = false;
  }

  Boolean lastAnswerCorrect = (Boolean) session.getAttribute("lastAnswerCorrect");
  String correctAnswer = (String) session.getAttribute("correctAnswer");
  String userAnswer = (String) session.getAttribute("userAnswer");

%>

<div class="quiz-container">
  <h1>Quiz</h1>
  <div class="progress-bar">
    <div class="progress-fill" style="width: <%= progressPercentage %>%"></div>
  </div>
  <p>Question <%= questionNumber %> of <%= totalQuestions %></p>

  <% if (showingFeedback) { %>
  <div class="feedback <%= lastAnswerCorrect ? "correct" : "incorrect" %>">
    <% if (lastAnswerCorrect) { %>
    <h3>✓ Correct!</h3>
    <p>Well done! You got it right.</p>
    <% } else { %>
    <h3>✗ Incorrect</h3>
    <p>Your answer: <%= userAnswer %></p>
    <p>Correct answer: <%= correctAnswer %></p>
    <% } %>
  </div>

  <div class="navigation">
    <% if (questionNumber <= totalQuestions) { %>
    <form action="TakeQuizServlet" method="post">
      <input type="hidden" name="action" value="nextQuestion">
      <button type="submit" class="btn btn-primary">Continue</button>
    </form>
    <% } else { %>
    <form action="TakeQuizServlet" method="post">
      <input type="hidden" name="action" value="finishQuiz">
      <button type="submit" class="btn btn-primary">View Results</button>
    </form>
    <% } %>
  </div>

  <% } else if (questionNumber <= questions.size()) { %>
  <%
    Question currentQuestion = questions.get(questionNumber - 1);
  %>
  <form action="TakeQuizServlet" method="post">
    <div class="question">
      <div class="questionType"><%= currentQuestion.getType() %> Question</div>

      <% if (currentQuestion.getType().equals(Constantas.MULTIPLE_CHOICE)) { %>
      <div class="questionText"><%= currentQuestion.getPrompt() %></div>
      <div class="answer-option">
        <%
          MultipleChoiceQuestion quest = (MultipleChoiceQuestion) currentQuestion;
          List<String> answers = quest.get_possible_answers();
          session.setAttribute("mcq", quest);
          for (int i = 0; i < answers.size(); i++) {
            String option = answers.get(i);
        %>

        <label>
          <input type="radio" name="answer" value="<%= i %>" id="option<%= i %>" required>
          <%= option %>
        </label><br>
        <% } %>
      </div>

      <% } else if (currentQuestion.getType().equals(Constantas.PICTURE_RESPONSE)) { %>
      <div class="pic">
        <img src="<%= currentQuestion.getPrompt() %>"/>
      </div>
      <div class="answer-option">
        <textarea name="answer" placeholder="Enter your response here..." required></textarea>
      </div>

      <% } else if (currentQuestion.getType().equals(Constantas.QUESTION_RESPONSE)) { %>
      <div class="questionText"><%= currentQuestion.getPrompt() %></div>
      <div class="answer-option">
        <textarea name="answer" placeholder="Enter your response here..." required></textarea>
      </div>

      <% } else if (currentQuestion.getType().equals(Constantas.FILL_IN_THE_BLANK)) { %>
      <div class="fill-in-blank">
        <%
          String questionPrompt = currentQuestion.getPrompt();
          int blankIndex = questionPrompt.indexOf("_");
          if (blankIndex != -1) {
            String start = questionPrompt.substring(0, blankIndex);
            String end = questionPrompt.substring(blankIndex + 1);
        %>
        <div class="answer-option">
          <%= start %>
          <textarea name="answer" placeholder="Enter your response here..." required></textarea>
          <%= end %>
        </div>
        <% } else { %>
        <div class="questionText"><%= questionPrompt %></div>
        <div class="answer-option">
          <input type="text" name="answer" placeholder="Enter your answer" required>
        </div>
        <% } %>
      </div>
      <% } %>

      <input type="hidden" name="questionId" value="<%= currentQuestion.getQuestionId() %>">
      <input type="hidden" name="questionNumber" value="<%= questionNumber %>">
      <input type="hidden" name="action" value="submitAnswer">
    </div>

    <div class="navigation">
        <button type="submit" class="btn btn-primary">Submit Answer</button>
    </div>
  </form>

  <% } else { %>
  <div class="question">
    <h2>Quiz Completed!</h2>
    <p>Thank you for taking the quiz.</p>
    <a href="results.jsp" class="btn btn-primary">View Results</a>
  </div>
  <% } %>
</div>

</body>
</html>