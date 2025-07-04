package Servlets;

import AccountManager.Account;
import AccountManager.Message;
import Daos.CommunicationDao;
import Daos.HistoryDao;
import Daos.QuizDao;
import Daos.UsersDao;
import quiz.history.History;
import quiz.quiz.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/MainPageServlet")
public class MainPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        Account user = (Account) req.getSession().getAttribute("user");
        if (user == null) {
            res.sendRedirect("index.jsp");
            return;
        }
        System.out.println("User ID: " + user.getId());
        CommunicationDao commDao=(CommunicationDao)getServletContext().getAttribute("commDao");
        HistoryDao histDao=(HistoryDao)getServletContext().getAttribute("histDao");
        QuizDao quizDao=(QuizDao)getServletContext().getAttribute("quizDao");
        UsersDao userDao=(UsersDao)getServletContext().getAttribute("accountDB");
        if (commDao == null || histDao == null || quizDao == null || userDao == null) {
            res.sendRedirect("index.jsp");
            return;
        }
        ArrayList<String> announcements=null;
        History takenHistory=null;
        History createdHistory=null;
        ArrayList<String> achievements=null;
        ArrayList<Quiz> recentQuizzes=null;
        ArrayList<Message> receivedMessages=null;
        ArrayList<Quiz> most_popular_quizzes=null;
        ArrayList<Account> searchResults = null;

        String searchQuery = req.getParameter("search");

        try {
            announcements=userDao.getAnnouncements();
            takenHistory=histDao.getUserHistory(user.getId());
            createdHistory=histDao.getUserCreatingHistory(user.getId());
            achievements=userDao.getAchievements(user.getId());
            recentQuizzes=quizDao.getQuizes();
            receivedMessages=commDao.getAllGottenMessages(user.getId());
            most_popular_quizzes=quizDao.getPopularQuizes();

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                searchResults = searchUsers(userDao, searchQuery.trim(), user.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("announcements", announcements);
        req.setAttribute("takenHistory", takenHistory);
        req.setAttribute("createdHistory", createdHistory);
        req.setAttribute("achievements", achievements);
        req.setAttribute("recentQuizzes", recentQuizzes);
        req.setAttribute("receivedMessages", receivedMessages);
        req.setAttribute("mostPopularQuizzes", most_popular_quizzes);
        req.setAttribute("searchResults", searchResults);
        req.setAttribute("searchQuery", searchQuery);

        req.getRequestDispatcher("mainPage.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }

    private ArrayList<Account> searchUsers(UsersDao userDao, String searchQuery, int currentUserId)
            throws SQLException {
        ArrayList<Account> results = new ArrayList<>();

        try {
            results = userDao.searchUsersByUsername(searchQuery, currentUserId);
        } catch (Exception e) {
            System.out.println("Search method not implemented in UsersDao: " + e.getMessage());
        }

        return results;
    }
}
