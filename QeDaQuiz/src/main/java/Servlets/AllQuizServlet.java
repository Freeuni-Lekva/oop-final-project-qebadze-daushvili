package Servlets;

import Daos.AdminDao;
import Daos.QuizDao;
import quiz.questions.Question;
import quiz.quiz.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AllQuizServlet")
public class AllQuizServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        AdminDao adminDao=(AdminDao)getServletContext().getAttribute("adminDao");
        String button = req.getParameter("action");
        QuizDao db = (QuizDao) req.getServletContext().getAttribute("quizDao");
        if(button.equals("Rank by release date")){
            try {
                ArrayList<Quiz> list = db.getQuizes();
                req.setAttribute("list", list);
                req.getRequestDispatcher("allQuizes.jsp").forward(req, res);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(button.equals("Rank by popularity")){
            try {
                ArrayList<Quiz> list = db.getPopularQuizes();
                req.setAttribute("list", list);
                req.getRequestDispatcher("allQuizes.jsp").forward(req, res);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        if (button.equals("Remove Quiz")) {
            // Handle quiz removal
            String quizIdParam = req.getParameter("quizId");
            if (quizIdParam != null) {
                int quizId = Integer.parseInt(quizIdParam);
                try {
                    adminDao.removeQuiz(quizId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            // Refresh list after removal
            ArrayList<Quiz> list = null;
            try {
                list = db.getQuizes();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            req.setAttribute("list", list);
            req.getRequestDispatcher("allQuizes.jsp").forward(req, res);
        }
        if(button.equals("Go to main page")){
            req.getRequestDispatcher("MainPageServlet").forward(req, res);
        }
        if(button.equals("Search")){
            String quizIdParam = req.getParameter("quiz search");
            try {
                List<Quiz> list = db.getQuizByName(quizIdParam);
                req.setAttribute("list", list);
                req.getRequestDispatcher("allQuizes.jsp").forward(req, res);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
