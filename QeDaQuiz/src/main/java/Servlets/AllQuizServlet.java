package Servlets;

import Daos.QuizDao;
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

@WebServlet("/AllQuizServlet")
public class AllQuizServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
    }
}
