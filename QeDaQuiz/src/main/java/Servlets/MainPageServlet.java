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
        try {
            announcements=userDao.getAnnouncements();
            takenHistory=histDao.getUserHistory(user.getId());
            createdHistory=histDao.getUserCreatingHistory(user.getId());
            achievements=userDao.getAchievements(user.getId());
            recentQuizzes=quizDao.getQuizes();
            receivedMessages=commDao.getAllGottenMessages(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("announcements", announcements);
        req.setAttribute("takenHistory", takenHistory);
        req.setAttribute("createdHistory", createdHistory);
        req.setAttribute("achievements", achievements);
        req.setAttribute("recentQuizzes", recentQuizzes);
        req.setAttribute("receivedMessages", receivedMessages);
        req.getRequestDispatcher("mainPage.jsp").forward(req, res);
    }
}
