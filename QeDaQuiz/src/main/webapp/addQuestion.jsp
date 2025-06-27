<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Question</title>
</head>
<body>

<% Integer questionNumber = (Integer) session.getAttribute("questionNumber"); %>
<p>Question #<%= questionNumber != null ? questionNumber : 1 %></p>

<form method="POST" action="CreateQuizServlet">
    <input type="hidden" name="page" value="addQuestion">
    <label for="quizType">Select Question Type:</label>
    <select name="questionType" id="quizType">
        <option value="multipleChoice">Multiple Choice Question</option>
        <option value="response">Response Question</option>
    </select>
    <input type="submit" name="addQuestion" value="Add Question" />
</form>

</body>
</html>
