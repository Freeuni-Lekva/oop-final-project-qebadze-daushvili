package Servlets;

import Daos.UsersDao;
import quiz.questions.Question;
import Daos.QuizDao;
import quiz.questions.MultipleChoiceQuestion;
import Constantas.Constantas;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/TakeQuizServlet")
public class TakeQuizServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        try {
            QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
            int quizId = (int) session.getAttribute("quizId");
            List<Question> questions = quizDao.getQuizQuestions(quizId);

            if ("submitAnswer".equals(action)) {
                answerSubmission(request, response, session, questions, quizDao);
            } else if ("nextQuestion".equals(action)) {
                nextQuestion(request, response, session, questions);
            } else if ("finishQuiz".equals(action)) {
                finishQuiz(request, response, session);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    private void answerSubmission(HttpServletRequest request, HttpServletResponse response, HttpSession session, List<Question> questions, QuizDao quizDao)
            throws ServletException, IOException, SQLException {
        if(session.getAttribute("userAnswers") == null) {
            List<AbstractMap.SimpleEntry<String, Question>> answers = new ArrayList<>();
            session.setAttribute("userAnswers", answers);
        }
        int questionNumber = (int) session.getAttribute("questionNumber");
        Question currentQuestion = questions.get(questionNumber - 1);
        String userAnswer = request.getParameter("answer");
        System.out.println(userAnswer);
        if (session.getAttribute("score") == null) {
            session.setAttribute("score", 0);
        }
        boolean isCorrect = false;
        String correctAnswer = "";

        if (currentQuestion.getType().equals(Constantas.MULTIPLE_CHOICE)) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) session.getAttribute("mcq");
            int selectedIndex;
            try {
                selectedIndex = Integer.parseInt(userAnswer);
            } catch (NumberFormatException e) {
                selectedIndex = -1;
            }
            System.out.println(mcq.get_correct_answer_index());
            isCorrect = selectedIndex == mcq.get_correct_answer_index();
            correctAnswer = mcq.get_possible_answers().get(mcq.get_correct_answer_index());
            userAnswer = mcq.get_possible_answers().get(selectedIndex);
            AbstractMap.SimpleEntry ent = new AbstractMap.SimpleEntry<>(userAnswer, mcq);
            session.setAttribute("userAnswers", ((List<AbstractMap.SimpleEntry<String, Question>>) session.getAttribute("userAnswers")).add(ent));
        } else {
            List<String> correctAnswers = currentQuestion.getCorrectAnswers();
            for (String answer : correctAnswers) {
                if (answer.trim().equalsIgnoreCase(userAnswer.trim())) {
                    isCorrect = true;
                    break;
                }
            }
            correctAnswer = correctAnswers.get(0);
            AbstractMap.SimpleEntry ent = new AbstractMap.SimpleEntry<>(userAnswer, currentQuestion);
            session.setAttribute("userAnswers", ((List<AbstractMap.SimpleEntry<String, Question>>) session.getAttribute("userAnswers")).add(ent));
        }
        if (isCorrect) {
            session.setAttribute("score", (int)session.getAttribute("score")+1);
        }

        session.setAttribute("lastAnswerCorrect", isCorrect);
        session.setAttribute("correctAnswer", correctAnswer);
        session.setAttribute("userAnswer", userAnswer);
        session.setAttribute("showingFeedback", true);
        response.sendRedirect("takeQuiz.jsp");
    }

    private void nextQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session, List<Question> questions)
            throws IOException {
        int questionNumber = (int) session.getAttribute("questionNumber");
        questionNumber++;
        if (questionNumber <= questions.size()) {
            session.setAttribute("questionNumber", questionNumber);
            session.setAttribute("showingFeedback", false);
        }
        response.sendRedirect("takeQuiz.jsp");
    }

    private void finishQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, SQLException {
        int userId = (int) session.getAttribute("userId");
        int quizId = (int) session.getAttribute("quizId");
        UsersDao userDao = (UsersDao) getServletContext().getAttribute("accountDB");
        int score = (int) session.getAttribute("score");
        session.removeAttribute("score");
        session.setAttribute("quizScore", score);
        userDao.takeQuiz(userId, quizId, score);
        response.sendRedirect("resultPage.jsp");
    }
}
