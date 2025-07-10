<%@ page import="quiz.questions.Question" %>
<%@ page import="quiz.questions.MultipleChoiceQuestion" %>
<%@ page import="java.util.List" %>
<%@ page import="Constantas.Constantas" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String questionType = request.getParameter("questionType");
  boolean showQuestionForm = "true".equals(request.getParameter("showQuestionForm"));
  boolean continueAdding = "true".equals(request.getParameter("continueAdding"));
  Integer editQuestionNumber = (Integer) session.getAttribute("editQuestionNumber");
  boolean isEditing = editQuestionNumber != null;
%>

<html>
<head>
  <title>Create New Quiz</title>
  <link rel="stylesheet" type="text/css" href="/css/createQuiz.css?v=245">
</head>
<body>

<div class="createQuizContainer">
  <h1>Create New Quiz</h1>
  <div class="quizInfo">
    <%
      String quizName = (String) session.getAttribute("quizName");
      String quizDescription = (String) session.getAttribute("quizDescription");
      boolean quizInfoSubmitted = quizName != null && quizDescription != null;
    %>

    <form method="POST" action="CreateQuizServlet">
      <label for="quizName">Quiz Name: </label>
      <input type="text" id="quizName" name="quizName"
             value="<%= quizInfoSubmitted ? quizName : "" %>" <%= quizInfoSubmitted ? "readonly" : "required" %>>
      <% if (quizInfoSubmitted) { %>
      <button type="button" onclick="enableEdit('quizName')">Edit</button>
      <% } %>
      <br><br>
      <label for="quizDescription">Quiz Description: </label>
      <input type="text" id="quizDescription" name="quizDescription"
             value="<%= quizInfoSubmitted ? quizDescription : "" %>" <%= quizInfoSubmitted ? "readonly" : "required" %>>
      <% if (quizInfoSubmitted) { %>
      <button type="button" onclick="enableEdit('quizDescription')">Edit</button>
      <% } %>
      <br><br>
      <% if (!quizInfoSubmitted) { %>
      <button type="submit" name="submitQuizInfo">Next</button>
      <% } else { %>
      <button type="submit" name="updateQuizInfo">Update Info</button>
      <% } %>
    </form>
  </div>

  <script>
    function enableEdit(fieldId) {
      const field = document.getElementById(fieldId);
      field.readOnly = false;
      field.focus();
    }
  </script>

  <%
    if (quizInfoSubmitted) {
      List<Question> questions = (List<Question>) session.getAttribute("questions");
  %>
  <div class="questions">
    <% if (questions != null && !questions.isEmpty()) { %>
    <div class="previous-questions">
      <% for(int i=0; i<questions.size(); i++) {
        Question currentQuestion = questions.get(i);
        if (currentQuestion != null) {
      %>
      <div class="questionItem">
        <div class="question-actions">
          <form method="POST" action="CreateQuizServlet" style="display: inline;">
            <input type="hidden" name="editQuestionNumber" value="<%=i%>">
            <button type="submit" name="editQuestion">Edit</button>
          </form>
          <form method="POST" action="CreateQuizServlet" style="display: inline;">
            <input type="hidden" name="deleteQuestionNumber" value="<%=i%>">
            <button type="submit" name="deleteQuestion">Delete</button>
          </form>
        </div>
        <strong>Question <%=i+1%>:</strong>
        <%
          String type = currentQuestion.getType();
          String prompt = currentQuestion.getPrompt();
        %>
        <p>Type: <%= type %></p>
        <% if (type.equals(Constantas.MULTIPLE_CHOICE) && currentQuestion instanceof MultipleChoiceQuestion) {
          List<String> correct = currentQuestion.getCorrectAnswers();
          List<String> wrong = ((MultipleChoiceQuestion) currentQuestion).getWrongAnswers();
        %>
        <p>Question: <%= prompt %></p>
        <p><strong>Correct Answer:</strong> <%= correct.get(0) %></p>
        <p><strong>Wrong Answers:</strong></p>
        <ul>
          <% for (String ans : wrong) { %>
          <li><%= ans %></li>
          <% } %>
        </ul>
        <% } else if (type.equals(Constantas.QUESTION_RESPONSE) || type.equals(Constantas.FILL_IN_THE_BLANK)) {
          List<String> correct = currentQuestion.getCorrectAnswers();
        %>
        <p>Question: <%= prompt %></p>
        <p><strong>Possible Answers:</strong></p>
        <ul>
          <% for (String ans : correct) { %>
          <li><%= ans %></li>
          <% } %>
        </ul>
        <% } else if (type.equals(Constantas.PICTURE_RESPONSE)) {
          List<String> correct = currentQuestion.getCorrectAnswers();
        %>
        <p>Picture:</p>
        <div class="pic">
          <img src="<%= prompt%>"/>
        </div>
        <p><strong>Possible Responses:</strong></p>
        <ul>
          <% for (String ans : correct) { %>
          <li><%= ans %></li>
          <% } %>
        </ul>
        <% } %>
      </div>
      <% }
      } %>
    </div>
    <% } %>

    <div class="newQuestion">
      <% if (!showQuestionForm && !continueAdding && !isEditing) { %>
      <form method="POST" action="CreateQuizServlet">
        <input type="hidden" name="addQuestion" value="addQuestion">
        <label for="quizType">Select Question Type:</label>
        <select name="questionType" id="quizType">
          <option value="<%= Constantas.MULTIPLE_CHOICE %>"><%= Constantas.MULTIPLE_CHOICE %></option>
          <option value="<%= Constantas.QUESTION_RESPONSE %>"><%= Constantas.QUESTION_RESPONSE %></option>
          <option value="<%= Constantas.PICTURE_RESPONSE %>"><%= Constantas.PICTURE_RESPONSE %></option>
          <option value="<%= Constantas.FILL_IN_THE_BLANK %>"><%= Constantas.FILL_IN_THE_BLANK %></option>
        </select>
        <input type="hidden" name="showQuestionForm" value="true">
        <input type="submit" value="Add Question" />
      </form>
      <% } else {
        Question editQuestion;
        String currentQuestionType;
        if (isEditing) {
          editQuestion = questions.get(editQuestionNumber);
          currentQuestionType = editQuestion.getType();
        }
        else {
          editQuestion = null;
          currentQuestionType = questionType;
        }
      %>
      <% if (Constantas.MULTIPLE_CHOICE.equals(currentQuestionType)) {
        String correctAnswer ="";
        List<String> wrongAnswers = null;
        if (isEditing && !editQuestion.getCorrectAnswers().isEmpty()) {
          correctAnswer = editQuestion.getCorrectAnswers().get(0);
        }
        if (isEditing) {
          wrongAnswers =  ((MultipleChoiceQuestion) editQuestion).getWrongAnswers();
        }
      %>
      <form method="POST" action="CreateQuizServlet">
        <input type="hidden" name="questionType" value="<%= Constantas.MULTIPLE_CHOICE %>">
        <% if (isEditing) { %>
        <input type="hidden" name="editQuestionNumber" value="<%= editQuestionNumber %>">
        <% } %>

        <label>Question:</label>
        <input type="text" name="question" placeholder="Enter question text"
               value="<%= isEditing ? editQuestion.getPrompt() : "" %>" required>
        <br><br>

        <label>Correct Answer:</label>
        <input type="text" name="correctAnswer" placeholder="Enter correct answer"
               value="<%= correctAnswer %>" required>
        <br><br>

        <label>Wrong Answers:</label>
        <div id="wrongAnswers">
          <% if (isEditing && wrongAnswers != null) {
            for (String answer : wrongAnswers) { %>
          <input type="text" name="wrongAnswer" value="<%= answer %>" required>
          <br>
          <% } } else { %>
          <input type="text" name="wrongAnswer" placeholder="Enter wrong answer" required>
          <br>
          <% } %>
        </div>
        <button type="button" onclick="addWrongAnswer()">Add Another Wrong Answer</button>
        <br><br>

        <input type="submit" name="<%= isEditing ? "updateQuestion" : "addMoreQuestions" %>"
               value="<%= isEditing ? "Update Question" : "Continue Adding Questions" %>">
        <input type="submit" name="submitQuiz" value="Submit Quiz">
      </form>
      <% } else if (Constantas.QUESTION_RESPONSE.equals(currentQuestionType)) {
        List<String> correctAnswers = null;
        if (isEditing) correctAnswers = editQuestion.getCorrectAnswers();
      %>
      <form method="POST" action="CreateQuizServlet">
        <input type="hidden" name="questionType" value="<%= Constantas.QUESTION_RESPONSE %>">
        <% if (isEditing) { %>
        <input type="hidden" name="editQuestionNumber" value="<%= editQuestionNumber %>">
        <% } %>

        <label>Question:</label>
        <input type="text" name="question" placeholder="Enter question text"
               value="<%= isEditing ? editQuestion.getPrompt() : "" %>" required>
        <br><br>

        <label>Correct Answers:</label>
        <div id="correctAnswers">
          <% if (isEditing) {
            for (String answer : correctAnswers) { %>
          <input type="text" name="correctAnswer" value="<%= answer %>" required>
          <br>
          <% } } else { %>
          <input type="text" name="correctAnswer" placeholder="Enter correct answer" required>
          <br>
          <% } %>
        </div>
        <button type="button" onclick="addCorrectAnswer()">Add Another Correct Answer</button>
        <br><br>

        <input type="submit" name="<%= isEditing ? "updateQuestion" : "addMoreQuestions" %>"
               value="<%= isEditing ? "Update Question" : "Continue Adding Questions" %>">
        <input type="submit" name="submitQuiz" value="Submit Quiz">
      </form>
      <% } else if (Constantas.PICTURE_RESPONSE.equals(currentQuestionType)) {
        List<String> correctAnswers = null;
        String imageUrl = "";
        if (isEditing) {
          correctAnswers = editQuestion.getCorrectAnswers();
          imageUrl = editQuestion.getPrompt();
        }
      %>
      <form method="POST" action="CreateQuizServlet">
        <input type="hidden" name="questionType" value="<%= Constantas.PICTURE_RESPONSE %>">
        <% if (isEditing) { %>
        <input type="hidden" name="editQuestionNumber" value="<%= editQuestionNumber %>">
        <% } %>

        <label>Image URL:</label>
        <input type="text" name="question" placeholder="Enter image url"
               value="<%= isEditing ? editQuestion.getPrompt() : "" %>" required>
        <br><br>

        <label>Correct Answers:</label>
        <div id="correctAnswers">
          <% if (isEditing) {
            for (String answer : correctAnswers) { %>
          <input type="text" name="correctAnswer" value="<%= answer %>" required>
          <br>
          <% } } else { %>
          <input type="text" name="correctAnswer" placeholder="Enter correct answer" required>
          <br>
          <% } %>
        </div>
        <button type="button" onclick="addCorrectAnswer()">Add Another Correct Answer</button>
        <br><br>

        <input type="submit" name="<%= isEditing ? "updateQuestion" : "addMoreQuestions" %>"
               value="<%= isEditing ? "Update Question" : "Continue Adding Questions" %>">
        <input type="submit" name="submitQuiz" value="Submit Quiz">
      </form>
      <% } else if (Constantas.FILL_IN_THE_BLANK.equals(currentQuestionType)) {
        List<String> correctAnswers = null;
        if (isEditing) correctAnswers = editQuestion.getCorrectAnswers();
      %>
      <form method="POST" action="CreateQuizServlet">
        <input type="hidden" name="questionType" value="<%= Constantas.FILL_IN_THE_BLANK %>">
        <% if (isEditing) { %>
        <input type="hidden" name="editQuestionNumber" value="<%= editQuestionNumber %>">
        <% } %>

        <label>Question:</label>
        <input type="text" name="question" placeholder="Enter question text"
               value="<%= isEditing ? editQuestion.getPrompt() : "" %>" required>
        <p>Use '_' for the blank.</p>
        <br><br>

        <label>Correct Answers:</label>
        <div id="correctAnswers">
          <% if (isEditing) {
            for (String answer : correctAnswers) { %>
          <input type="text" name="correctAnswer" value="<%= answer %>" required>
          <br>
          <% } } else { %>
          <input type="text" name="correctAnswer" placeholder="Enter correct answer" required>
          <br>
          <% } %>
        </div>
        <button type="button" onclick="addCorrectAnswer()">Add Another Correct Answer</button>
        <br><br>
        <input type="submit" name="<%= isEditing ? "updateQuestion" : "addMoreQuestions" %>"
               value="<%= isEditing ? "Update Question" : "Continue Adding Questions" %>">
        <input type="submit" name="submitQuiz" value="Submit Quiz">
      </form>
      <%
      }%>
      <% } %>
    </div>
  </div>
  <% } %>
</div>
<script>
  function addWrongAnswer() {
    const container = document.getElementById('wrongAnswers');
    addAnswerField(container, 'wrongAnswer', 'Enter wrong answer');
  }

  function addCorrectAnswer() {
    const container = document.getElementById('correctAnswers');
    addAnswerField(container, 'correctAnswer', 'Enter correct answer');
  }

  function addAnswerField(container, name, placeholder) {
    const input = document.createElement('input');
    input.type = 'text';
    input.name = name;
    input.placeholder = placeholder;
    input.required = true;
    container.appendChild(document.createElement('br'));
    container.appendChild(input);
  }
</script>
</body>
</html>