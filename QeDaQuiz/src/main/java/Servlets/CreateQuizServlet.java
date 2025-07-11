package Servlets;

import AccountManager.Account;
import Constantas.Constantas;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession();
        List<Question> questions = (List<Question>) session.getAttribute("questions");

        if (questions == null) {
            questions = new ArrayList<>();
            session.setAttribute("questions", questions);
        }

        if (req.getParameter("submitQuizInfo") != null) {
            String quizName = req.getParameter("quizName");
            String quizDescription = req.getParameter("quizDescription");
            session.setAttribute("quizName", quizName);
            session.setAttribute("quizDescription", quizDescription);
            String questionOrder = req.getParameter("questionOrder");
            session.setAttribute("questionOrder", questionOrder);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("editQuestion") != null) {
            int questionNumber = Integer.parseInt(req.getParameter("editQuestionNumber"));
            session.setAttribute("editQuestionNumber", questionNumber);
            session.setAttribute("showQuestionForm", true);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("deleteQuestion") != null) {
            int questionNumber = Integer.parseInt(req.getParameter("deleteQuestionNumber"));
            questions.remove(questionNumber);
            session.setAttribute("questions", questions);
            session.removeAttribute("editQuestionNumber");
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("addQuestion") != null) {
            session.removeAttribute("editQuestionNumber");
            session.setAttribute("showQuestionForm", true);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("updateQuestion") != null) {
            int editNumber = Integer.parseInt(req.getParameter("editQuestionNumber"));
            addOrEditQuestion(req, questions, editNumber);
            session.removeAttribute("editQuestionNumber");
            session.setAttribute("showQuestionForm", false);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("addMoreQuestions") != null) {
            addOrEditQuestion(req, questions, -1);
            session.setAttribute("showQuestionForm", true);
            session.setAttribute("continueAdding", true);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        if (req.getParameter("submitQuiz") != null) {
            if (session.getAttribute("editQuestionNumber") != null) {
                int editNumber = (Integer) session.getAttribute("editQuestionNumber");
                addOrEditQuestion(req, questions, editNumber);
                session.removeAttribute("editQuestionNumber");
            } else {
                addOrEditQuestion(req, questions, -1);
            }
            try {
                submitQuiz(req, res, session, questions);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (req.getParameter("updateQuizInfo") != null) {
            String quizName = req.getParameter("quizName");
            String quizDescription = req.getParameter("quizDescription");
            session.setAttribute("quizName", quizName);
            String questionOrder = req.getParameter("questionOrder");
            session.setAttribute("quizDescription", quizDescription);
            session.setAttribute("questionOrder", questionOrder);
            req.getRequestDispatcher("createQuiz.jsp").forward(req, res);
            return;
        }

        req.getRequestDispatcher("createQuiz.jsp").forward(req, res);

    }

    private void addOrEditQuestion(HttpServletRequest req, List<Question> questions, int questionNumber) throws ServletException {
        String questionType = req.getParameter("questionType");
        String questionText = req.getParameter("question");
        String[] correctAnswers = req.getParameterValues("correctAnswer");

        if (questionText==null || questionText.isEmpty() || correctAnswers==null || correctAnswers.length==0) {
            throw new ServletException("Question text and a correct answer are required");
        }
        List<String> correctAnswersList = new ArrayList<>(Arrays.asList(correctAnswers));

        try {
            if (Constantas.MULTIPLE_CHOICE.equals(questionType)) {
                String[] wrongAnswers = req.getParameterValues("wrongAnswer");
                List<String> wrongAnswersList = new ArrayList<>();
                if (wrongAnswers!=null) {
                    wrongAnswersList.addAll(Arrays.asList(wrongAnswers));
                }
                if (wrongAnswersList.isEmpty()) {
                    throw new ServletException("Multiple choice questions require at least one wrong answer");
                }
                Question question = new MultipleChoiceQuestion(questionText, correctAnswersList, wrongAnswersList, questionType);
                if (questionNumber >= 0) {
                    questions.set(questionNumber, question);
                } else {
                    questions.add(question);
                }
            } else if (Constantas.QUESTION_RESPONSE.equals(questionType) || Constantas.PICTURE_RESPONSE.equals(questionType) || Constantas.FILL_IN_THE_BLANK.equals(questionType)) {
                Question question = new ResponseQuestion(questionText, correctAnswersList, questionType);
                if (questionNumber >= 0) {
                    questions.set(questionNumber, question);
                } else {
                    questions.add(question);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error creating question", e);
        }
    }

    private void submitQuiz(HttpServletRequest req, HttpServletResponse res, HttpSession session, List<Question> questions)
            throws ServletException, IOException, SQLException {
        String quizName = (String) session.getAttribute("quizName");
        String quizDescription = (String) session.getAttribute("quizDescription");
        Account user = (Account) session.getAttribute("user");
        QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");
        String questionOrder = (String) session.getAttribute("questionOrder");
        boolean isRandom = questionOrder.equals("random");
        Quiz quiz = new Quiz(quizDao.numberOfQuestions(), quizName, quizDescription, user.getId(), questions, isRandom);
        try {
            quizDao.addQuiz(quiz);
            UsersDao usersDao = (UsersDao) getServletContext().getAttribute("accountDB");
            usersDao.makeQuiz(quiz);
            session.removeAttribute("questions");
            session.removeAttribute("quizName");
            session.removeAttribute("quizDescription");
            session.removeAttribute("editQuestionNumber");
            session.removeAttribute("showQuestionForm");
            session.removeAttribute("continueAdding");
            session.removeAttribute("questionOrder");
            res.sendRedirect("MainPageServlet");
        } catch (SQLException e) {
            throw new ServletException("Database error while saving quiz", e);
        }
    }
}