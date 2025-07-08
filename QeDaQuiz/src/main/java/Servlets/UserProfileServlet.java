package Servlets;

import AccountManager.Account;
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

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Check if current user is logged in
        Account currentUser = (Account) req.getSession().getAttribute("user");
        if (currentUser == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        // Get the user ID from the request parameter
        String userIdParam = req.getParameter("userId");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            res.sendRedirect("MainPageServlet");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            res.sendRedirect("MainPageServlet");
            return;
        }

        // Get DAOs from servlet context
        UsersDao userDao = (UsersDao) getServletContext().getAttribute("accountDB");
        HistoryDao histDao = (HistoryDao) getServletContext().getAttribute("histDao");
        QuizDao quizDao = (QuizDao) getServletContext().getAttribute("quizDao");

        if (userDao == null || histDao == null || quizDao == null) {
            res.sendRedirect("MainPageServlet");
            return;
        }

        try {
            // Get the profile user's information
            Account profileUser = userDao.getUser(userId);
            profileUser.setId(userId);
            if (profileUser == null) {
                req.setAttribute("error", "User not found");
                req.getRequestDispatcher("error.jsp").forward(req, res);
                return;
            }

            // Get user's statistics
            int quizzesTaken = userDao.getTakenQuizesQuantity(userId);
            int quizzesMade = userDao.getMadeQuizesQuantity(userId);
            ArrayList<String> achievements = userDao.getAchievements(userId);

            // Get user's quiz history (created quizzes only - taken quizzes might be private)
            History createdHistory = histDao.getUserCreatingHistory(userId);
            ArrayList<Quiz> createdQuizzes = new ArrayList<>();
            if (createdHistory != null) {
                for (int i = 1; i <= Math.min(10, createdHistory.getSize()); i++) {
                    createdQuizzes.add(createdHistory.getQuiz(i));
                }
            }

            // Get user's recent quizzes (alternative way if history doesn't work)
            ArrayList<Quiz> recentCreatedQuizzes = getRecentQuizzesByUser(quizDao, userId);

            // Set attributes for JSP
            req.setAttribute("profileUser", profileUser);
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("quizzesTaken", quizzesTaken);
            req.setAttribute("quizzesMade", quizzesMade);
            req.setAttribute("achievements", achievements);
            req.setAttribute("createdQuizzes", createdQuizzes);
            req.setAttribute("recentCreatedQuizzes", recentCreatedQuizzes);

            req.getRequestDispatcher("userProfile.jsp").forward(req, res);

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error occurred");
            req.getRequestDispatcher("error.jsp").forward(req, res);
        }
    }

    /**
     * Get recent quizzes created by a specific user
     * You might need to add this method to your QuizDao class
     */
    private ArrayList<Quiz> getRecentQuizzesByUser(QuizDao quizDao, int userId) {
        ArrayList<Quiz> userQuizzes = new ArrayList<>();
        try {
            // This assumes you have a method to get quizzes by user ID
            // If not, you can implement it in your QuizDao class
            userQuizzes = quizDao.getQuizzesByUserId(userId);
        } catch (Exception e) {
            System.out.println("Could not fetch user's quizzes: " + e.getMessage());
            // Return empty list if method doesn't exist yet
        }
        return userQuizzes;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Handle POST requests the same way as GET
        doGet(req, res);
    }
}