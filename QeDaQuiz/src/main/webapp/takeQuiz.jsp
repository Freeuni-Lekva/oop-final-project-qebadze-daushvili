<%@ page import="quiz.quiz.Quiz" %>
<%@ page import="quiz.questions.Question" %>
<%@ page import="Daos.QuizDao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="Constantas.Constantas" %>
<%@ page import="quiz.questions.MultipleChoiceQuestion" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Take Quiz</title>
  <link rel="stylesheet" type="text/css" href="/css/takeQuiz.css?v=23">
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
  if(session.getAttribute("questions") == null) {
    List<Question> questions = new ArrayList<Question>();
    try {
      questions = db.getQuizQuestions(quizId);
      if (db.getQuiz(quizId).isRandom()) Collections.shuffle(questions);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    session.setAttribute("questions",questions);
  }
  List<Question> questions = (List<Question>) session.getAttribute("questions");

  Integer questionNumber = (Integer) session.getAttribute("questionNumber");
  if (questionNumber == null) {
    questionNumber = 1;
    session.setAttribute("questionNumber", questionNumber);
  }
  int totalQuestions = questions.size();

  Boolean showingFeedback = (Boolean) session.getAttribute("showingFeedback");
  if (showingFeedback == null) {
    showingFeedback = false;
  }

  Boolean lastAnswerCorrect = (Boolean) session.getAttribute("lastAnswerCorrect");
  String correctAnswer = (String) session.getAttribute("correctAnswer");
  String userAnswer = (String) session.getAttribute("userAnswer");

%>

<div class="quiz-container">
  <div style="margin-bottom: 20px;">
    <form action="MainPageServlet" method="get">
      <div class="submit-section">
        <button type="submit" class="submit-btn">🔙 Main Page</button>
      </div>
    </form>
  </div>
  <div class="quiz-header">
    <h1><%= db.getQuiz(quizId).getQuizName() %></h1>
    <p class="quiz-description"><%= db.getQuiz(quizId).getQuizDescription() %></p>
    <div class="quiz-info">
      <span class="question-count">Question <%= questionNumber %> of <%= totalQuestions %></span>
    </div>
  </div>

  <% if (showingFeedback) { %>
  <div class="feedback <%= lastAnswerCorrect ? "correct" : "incorrect" %>">
    <% if (lastAnswerCorrect) { %>
    <div class="feedback-header">
      <h3>✅ Correct!</h3>
      <p>Well done! You got it right.</p>
    </div>
    <% } else { %>
    <div class="feedback-header">
      <h3>❌ Incorrect</h3>
      <div class="answer-info">
        <span class="answers">🔴 Your answer: <%= userAnswer %></span>
        <span class="answers">🟢 Correct answer: <%= correctAnswer %></span>
      </div>
    </div>
    <% } %>
  </div>

  <div class="navigation">
    <% if (questionNumber < totalQuestions) { %>
    <form action="TakeQuizServlet" method="post">
      <input type="hidden" name="action" value="nextQuestion">
      <div class="submit-section">
        <button type="submit" class="submit-btn">Continue</button>
      </div>
    </form>
    <% } else { %>
    <form action="TakeQuizServlet" method="post">
      <input type="hidden" name="action" value="finishQuiz">
      <div class="submit-section">
        <button type="submit" class="submit-btn">View Results</button>
      </div>
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
      <div class="question-text"><%= currentQuestion.getPrompt() %></div>
      <div class="answer-options">
        <%
          MultipleChoiceQuestion quest = (MultipleChoiceQuestion) currentQuestion;
          List<String> answers = quest.get_possible_answers();
          session.setAttribute("mcq", quest);
          for (int i = 0; i < answers.size(); i++) {
            String option = answers.get(i);
        %>

        <label class="option">
          <input type="radio" name="answer" value="<%= i %>" id="option<%= i %>" required>
          <span class="option-text"><%= option %></span>
        </label><br>
        <% } %>
      </div>

      <% } else if (currentQuestion.getType().equals(Constantas.PICTURE_RESPONSE)) { %>
      <div class="picture-container">
        <img src="<%= currentQuestion.getPrompt() %>"/>
      </div>
      <div class="answer-input">
        <input type="text" name="answer" placeholder="" required>
      </div>

      <% } else if (currentQuestion.getType().equals(Constantas.QUESTION_RESPONSE)) { %>
      <div class="question-text"><%= currentQuestion.getPrompt() %></div>
      <div class="answer-input">
        <input type="text" name="answer" placeholder="" required>
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
          <input type="text" name="answer" placeholder="" required>
          <%= end %>
        </div>
        <% } else { %>
        <div class="question-text"><%= questionPrompt %></div>
        <div class="answer-option">
          <input type="text" name="answer" placeholder="" required>
        </div>
        <% } %>
      </div>
      <% } %>

      <input type="hidden" name="questionId" value="<%= currentQuestion.getQuestionId() %>">
      <input type="hidden" name="questionNumber" value="<%= questionNumber %>">
      <input type="hidden" name="action" value="submitAnswer">
    </div>

    <div class="submit-section">
      <button type="submit" class="submit-btn">Submit Answer</button>
    </div>
  </form>

  <% } else { %>
  <div class="question">
    <h2>Quiz Completed!</h2>
    <p>Thank you for taking the quiz.</p>
    <a href="resultPage.jsp" class="btn btn-primary">View Results</a>
  </div>
  <% } %>
</div>

</body>
</html>