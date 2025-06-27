package Servlets;

import Daos.QuizDao;
import Daos.UsersDao;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;
import quiz.quiz.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/CreateQuizServlet")
public class CreateQuizServlet extends HttpServlet {

    private void submitQuiz (HttpServletRequest req, HttpServletResponse res, HttpSession session, List <Question> questions) throws ServletException {
        String quizName = req.getParameter("quizName");
        String quizDescription = req.getParameter("quizDescription");
        Quiz quiz = new Quiz (1, quizName, quizDescription, 1, questions);
        QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
        try {
            quizDao.addQuiz(quiz);
            session.invalidate();
            res.sendRedirect("quizCreated.jsp");
        } catch (SQLException | IOException e) {
            throw new ServletException("DB error", e);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String page = req.getParameter("page");
        if (page == null) page = "createQuiz";

        if (page.equals("createQuiz")) {
            String quizName = req.getParameter("quizName");
            String quizDescription = req.getParameter("quizDescription");
            if (quizName == null || quizDescription == null || quizName.equals("") || quizDescription.equals("")) {
                req.setAttribute("message", "Enter valid name & description!");
                req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
                return;
            }
            session.setAttribute("quizName", quizName);
            session.setAttribute("quizDescription", quizDescription);
            session.setAttribute("questions", new ArrayList<Question>());
            session.setAttribute("questionNumber", 1);
            req.getRequestDispatcher("addQuestion.jsp").forward(req, res);
        }

        if (page.equals("addQuestion")) {
            session.setAttribute("questionNumber", (Integer) session.getAttribute("questionNumber"));
            String questionType = req.getParameter("questionType");
            if (questionType.equals("multipleChoice")) {
                req.getRequestDispatcher("createMultipleChoiceQuestion.jsp").forward(req, res);
            } else {
                if (questionType.equals("response")) {
                    req.getRequestDispatcher("createResponseQuestion.jsp").forward(req, res);
                }
            }
            session.setAttribute("questionNumber", (Integer) session.getAttribute("questionNumber")+1);
        }

        if (page.equals("createMultipleChoiceQuestion")) {
            List <Question> questions = (List<Question>) session.getAttribute("questions");
            String question = req.getParameter("question");
            String correctAnswer = req.getParameter("correctAnswer");
            List <String> correctAnswers = new ArrayList<>();
            correctAnswers.add(correctAnswer);
            String[] wrongAnswers = req.getParameterValues("wrongAnswer");
            List <String> wrongAnswersList = new ArrayList<>();
            if (wrongAnswers != null) {
                wrongAnswersList.addAll(Arrays.asList(wrongAnswers));
            }
            Question quest = new MultipleChoiceQuestion(question,correctAnswers,wrongAnswersList,"multipleChoiceQuestion");
            questions.add(quest);
            String action = req.getParameter("submitQuiz") != null ? "submit" : "addMoreQuestions";
            if (action.equals("addMoreQuestions")) {
                req.getRequestDispatcher("addQuestion.jsp").forward(req, res);
            } else {
                submitQuiz(req, res, session, questions);
            }
        }

        if (page.equals("createResponseQuestion")) {
            List <Question> questions = (List<Question>) session.getAttribute("questions");
            String question = req.getParameter("question");
            String[] correctAnswers = req.getParameterValues("correctAnswer");
            List <String> correctAnswersList = new ArrayList<>();
            correctAnswersList.addAll(Arrays.asList(correctAnswers));
            Question quest = new ResponseQuestion(question,correctAnswersList,"responseQuestion");
            questions.add(quest);
            String action = req.getParameter("submitQuiz") != null ? "submit" : "addMoreQuestions";
            if (action.equals("addMoreQuestions")) {
                req.getRequestDispatcher("addQuestion.jsp").forward(req, res);
            } else {
                submitQuiz(req, res, session, questions);
            }
        }

    }

}