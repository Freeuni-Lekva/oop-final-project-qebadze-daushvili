<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Create Multiple Choice Question</title>
</head>
<body>

<% Integer questionNumber = (Integer) session.getAttribute("questionNumber"); %>
<p>Question #<%= questionNumber != null ? questionNumber : 1 %></p>

<form method="POST" action="CreateQuizServlet">
  <input type="hidden" name="page" value="createMultipleChoiceQuestion">
  <input type="text" name="question" placeholder="Enter Question:">
  <br></br>
  <input type="text" name="correctAnswer" placeholder="Enter Correct Answer:">
  <br></br>

  <div id="wrongAnswers">
    <input type="text" name="wrongAnswer" placeholder="Enter Wrong Answer:" required>
  </div>

  <button type="button" onclick="addWrongAnswer()">Add Wrong Answer</button>
  <br><br>

  <input type="submit" name="addMoreQuestions" value="Continue Adding Questions">
  <input type="submit" name="submitQuiz" value="Submit Quiz">
</form>

<script>
  function addWrongAnswer() {
    const container = document.getElementById('wrongAnswers');
    const input = document.createElement('input');
    input.type = 'text';
    input.name = 'wrongAnswer';
    input.placeholder = 'Enter Wrong Answer:';
    container.appendChild(document.createElement('br'));
    container.appendChild(input);
  }
</script>

</body>
</html>
