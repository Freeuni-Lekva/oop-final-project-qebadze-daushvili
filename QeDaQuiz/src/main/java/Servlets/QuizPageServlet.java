package Servlets;

import AccountManager.Account;
import Daos.HistoryDao;
import Daos.QuizDao;
import Daos.UsersDao;
import quiz.history.Stat;
import quiz.quiz.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@WebServlet("/QuizPageServlet")
public class QuizPageServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("startTime", Instant.now());
        res.sendRedirect("takeQuiz.jsp?quizId=" + req.getParameter("quizId"));
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int quizId = Integer.parseInt(req.getParameter("quizId"));
        String sortBy = req.getParameter("sortBy");

        // Get session user
        HttpSession session = req.getSession();
        Account user = (Account) session.getAttribute("user");

        // Get DAOs
        QuizDao db = (QuizDao) getServletContext().getAttribute("quizDao");
        UsersDao dbUsers = (UsersDao) getServletContext().getAttribute("accountDB");
        HistoryDao dbHist = (HistoryDao) getServletContext().getAttribute("histDao");

        // Fetch quiz & stats
        Quiz quiz = null;
        try {
            quiz = db.getQuiz(quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            Account creator = dbUsers.getUser(quiz.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        List<Stat> myStats = dbHist.getQuizStatsByUser(quizId, user.getId());

        if ("percent".equals(sortBy)) {
            myStats.sort((a, b) ->  Integer.compare(b.getPoints(), a.getPoints()));
        } else if ("time".equals(sortBy)) {
            myStats.sort((a, b) -> Long.compare(a.getTime(), b.getTime())); // shorter is better
        } else {
            myStats.sort((a, b) -> b.getLast().compareTo(a.getLast())); // latest first
        }
        req.setAttribute("myStats", myStats);
        req.getRequestDispatcher("/quizPage.jsp").forward(req, res);

    }
}
