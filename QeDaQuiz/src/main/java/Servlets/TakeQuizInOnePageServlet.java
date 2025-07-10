package Servlets;

import AccountManager.Account;
import Constantas.Constantas;
import Daos.QuizDao;
import Daos.UsersDao;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.quiz.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@WebServlet("/TakeQuizInOnePageServlet")
public class TakeQuizInOnePageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String quizId = req.getParameter("quizId");

        if (quizId == null || quizId.isEmpty()) {
            res.sendRedirect("mainPage.jsp?error=No quiz selected");
            return;
        }

        try {
            QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
            Quiz quiz = quizDao.getQuiz(Integer.parseInt(quizId));

            if (quiz == null) {
                res.sendRedirect("mainPage.jsp?error=Quiz not found");
                return;
            }

            req.getRequestDispatcher("takeQuiz.jsp").forward(req, res);

        } catch (SQLException e) {
            throw new ServletException("Database error while loading quiz", e);
        } catch (NumberFormatException e) {
            res.sendRedirect("mainPage.jsp?error=Invalid quiz ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int quizId = (int) session.getAttribute("quizId");
        QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
        Quiz quiz = null;
        try {
            quiz = quizDao.getQuiz(quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Account user = (Account) session.getAttribute("user");

        if (session.getAttribute("userAnswers") == null) {
            List<AbstractMap.SimpleEntry<String, Question>> answers = new ArrayList<>();
            session.setAttribute("userAnswers", answers);
        }

        if (session.getAttribute("score") == null) {
            session.setAttribute("score", 0);
        }

        if (quiz == null) {
            res.sendRedirect("mainPage.jsp?error=No quiz in session");
            return;
        }

        if (user == null) {
            res.sendRedirect("login.jsp?error=Please log in to take quiz");
            return;
        }

        // Calculate quiz results
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String userAnswer = req.getParameter("question_" + i);
            boolean isCorrect = false;

            if (userAnswer != null && !userAnswer.trim().isEmpty()) {
                if (question.getType().equals(Constantas.MULTIPLE_CHOICE)) {
                    String correctAnswer = question.getCorrectAnswers().get(0);
                    isCorrect = correctAnswer.equals(userAnswer);
                    AbstractMap.SimpleEntry ent = new AbstractMap.SimpleEntry<>(userAnswer, question);
                    List<AbstractMap.SimpleEntry<String, Question>> userAnswers =
                            (List<AbstractMap.SimpleEntry<String, Question>>) session.getAttribute("userAnswers");
                    userAnswers.add(ent);
                } else {
                    String correctAnswer = "";
                    List<String> correctAnswersList = question.getCorrectAnswers();
                    for (String answer : correctAnswersList) {
                        if (answer.trim().equalsIgnoreCase(userAnswer.trim())) {
                            isCorrect = true;
                            correctAnswer = answer;
                            break;
                        }
                    }
                    if (!isCorrect) correctAnswer = correctAnswersList.get(0);
                    AbstractMap.SimpleEntry ent = new AbstractMap.SimpleEntry<>(userAnswer, question);
                    List<AbstractMap.SimpleEntry<String, Question>> userAnswers =
                            (List<AbstractMap.SimpleEntry<String, Question>>) session.getAttribute("userAnswers");

                    userAnswers.add(ent);
                }
            }

            if (isCorrect) {
                session.setAttribute("score", (int)session.getAttribute("score")+1);
            }

        }

        UsersDao userDao = (UsersDao) getServletContext().getAttribute("accountDB");
        int score = (int) session.getAttribute("score");
        session.removeAttribute("score");
        session.setAttribute("quizScore", score);
        session.setAttribute("endTime", Instant.now());
        Instant startTime = (Instant) session.getAttribute("startTime");
        Instant endTime = (Instant) session.getAttribute("endTime");
        Duration duration = Duration.between(startTime, endTime);
        long secondsTaken = duration.getSeconds();
        Timestamp sqlStartTime = Timestamp.from(startTime);
        Timestamp sqlEndTime = Timestamp.from(endTime);
        try {
            userDao.takeQuiz(user.getId(), quizId, score, secondsTaken, sqlStartTime, sqlEndTime);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        res.sendRedirect("resultPage.jsp");
    }

}