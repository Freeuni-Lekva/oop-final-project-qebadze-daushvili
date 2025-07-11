package Servlets;

import AccountManager.Account;
import Constantas.Constantas;
import Daos.QuizDao;
import Daos.UsersDao;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/PracticeModeServlet")
public class PracticeModeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        try {
            QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
            List<Question> questions = (List<Question>) session.getAttribute("questions");

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
        int questionNumber = (int) session.getAttribute("questionNumber");
        Question currentQuestion = questions.get(questionNumber - 1);
        String userAnswer = request.getParameter("answer");
        boolean isCorrect = false;

        if (currentQuestion.getType().equals(Constantas.MULTIPLE_CHOICE)) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) session.getAttribute("mcq");
            int selectedIndex;
            try {
                selectedIndex = Integer.parseInt(userAnswer);
            } catch (NumberFormatException e) {
                selectedIndex = -1;
            }
            isCorrect = selectedIndex == mcq.get_correct_answer_index();
        } else {
            List<String> correctAnswers = currentQuestion.getCorrectAnswers();
            for (String answer : correctAnswers) {
                if (answer.trim().equalsIgnoreCase(userAnswer.trim())) {
                    isCorrect = true;
                    break;
                }
            }
        }
        session.setAttribute("lastAnswerCorrect", isCorrect);
        session.setAttribute("showingFeedback", true);
        response.sendRedirect("practiceMode.jsp");
    }

    private void nextQuestion(HttpServletRequest request, HttpServletResponse response, HttpSession session, List<Question> questions)
            throws IOException, SQLException {
        int questionNumber = (int) session.getAttribute("questionNumber");
        questionNumber++;
        if (questionNumber <= questions.size()) {
            session.setAttribute("questionNumber", questionNumber);
            session.setAttribute("showingFeedback", false);
        }
        response.sendRedirect("practiceMode.jsp");
    }

    private void finishQuiz(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, SQLException {
        Account user = (Account) session.getAttribute("user");
        int quizId = (int) session.getAttribute("quizId");
        UsersDao userDao = (UsersDao) getServletContext().getAttribute("accountDB");
        userDao.takeQuizInPracticeMode(user.getId(), quizId);
        session.removeAttribute("questions");
        response.sendRedirect("practiceModeResultPage.jsp");
    }

}
