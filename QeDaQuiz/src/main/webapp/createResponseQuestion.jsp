<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Create Response Question</title>
</head>
<body>
<% Integer questionNumber = (Integer) session.getAttribute("questionNumber"); %>
<p>Question #<%= questionNumber != null ? questionNumber : 1 %></p>

<form method="POST" action="CreateQuizServlet">
  <input type="hidden" name="page" value="createMultipleChoiceQuestion">
  <input type="text" name="question" placeholder="Enter Question:">
  <br></br>

  <div id="correctAnswers">
    <input type="text" name="correctAnswer" placeholder="Enter Possible Correct Answer:" required>
  </div>

  <button type="button" onclick="addCorrectAnswer()">Add Correct Answer</button>
  <br><br>

  <input type="submit" name="addMoreQuestions" value="Continue Adding Questions">
  <input type="submit" name="submitQuiz" value="Submit Quiz">
</form>

<script>
  function addCorrectAnswer() {
    const container = document.getElementById('correctAnswers');
    const input = document.createElement('input');
    input.type = 'text';
    input.name = 'correctAnswer';
    input.placeholder = 'Enter Correct Answer:';
    container.appendChild(document.createElement('br'));
    container.appendChild(input);
  }
</script>

</body>
</html>
