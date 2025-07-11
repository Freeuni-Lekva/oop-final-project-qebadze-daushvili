package Servlets;

import AccountManager.Account;
import AccountManager.Message;
import Daos.*;
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
import java.util.HashMap;

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
        AdminDao adminDao=(AdminDao)getServletContext().getAttribute("adminDao");
        if (commDao == null || histDao == null || quizDao == null || userDao == null|| adminDao == null) {
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
        ArrayList<Account> requests=null;
        ArrayList<Account> friends=null;
        Integer quantity_quizes_taken=0;
        Integer quantity_quizes_created=0;
        Boolean is_admin=false;
        ArrayList<ArrayList<Object> > abriviated_history=null;
        Integer num_of_received_messages=null;
        String searchQuery = req.getParameter("search");

        try {
            announcements=userDao.getAnnouncements();
            takenHistory=histDao.getUserHistory(user.getId());
            createdHistory=histDao.getUserCreatingHistory(user.getId());
            achievements=userDao.getAchievements(user.getId());
            recentQuizzes=quizDao.getQuizes();
            receivedMessages=commDao.getAllGottenMessages(user.getId());
            most_popular_quizzes=quizDao.getPopularQuizes();
            requests=commDao.getAllRequests(user.getId());
            friends=commDao.getAllFriends(user.getId());
            quantity_quizes_taken= userDao.getTakenQuizesQuantity(user.getId());
            quantity_quizes_created= userDao.getMadeQuizesQuantity(user.getId());
            is_admin=adminDao.isAdmin(user.getId());
            abriviated_history=new ArrayList<>();
            num_of_received_messages=userDao.getNumMessages(user.getId());
            for(Account friend:friends){
                int friend_id=friend.getId();
                ArrayList<Object> entries=new ArrayList<>();
                History friend_created_quizes= histDao.getUserCreatingHistory(friend_id);
                History friend_passed_quizes=histDao.getUserHistory(friend_id);
                ArrayList<String> friend_achievements=userDao.getAchievements(friend_id);
                if(friend_passed_quizes.getSize()!=0){
                    entries.add(friend_passed_quizes.getQuiz(1));
                }
                else{
                    entries.add(null);
                }
                if(friend_created_quizes.getSize()!=0){
                    entries.add(friend_passed_quizes.getQuiz(1));
                }
                else{
                    entries.add(null);
                }
                if(friend_achievements.size()!=0){
                    entries.add(friend_achievements.get(0));
                }
                else{
                    entries.add(null);
                }
                abriviated_history.add(entries);
            }
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
        req.setAttribute("requests", requests);
        req.setAttribute("friends", friends);
        req.setAttribute("quantity_quizes_taken", quantity_quizes_taken);
        req.setAttribute("quantity_quizes_created", quantity_quizes_created);
        req.setAttribute("is_admin", is_admin);
        req.setAttribute("abriviated_history", abriviated_history);
        req.setAttribute("num_of_received_messages", num_of_received_messages);
        req.getRequestDispatcher("mainPage.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        Account user = (Account) req.getSession().getAttribute("user");
        if (user == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        String action = req.getParameter("action");
        String userIdParam = req.getParameter("userId");

        if (action != null && userIdParam != null) {
            try {
                int requesterId = Integer.parseInt(userIdParam);
                CommunicationDao commDao = (CommunicationDao) getServletContext().getAttribute("commDao");
                if ("accept".equals(action)) {
                    commDao.accept_request(user.getId(), requesterId);
                } else if ("reject".equals(action)) {
                    commDao.decline_friend_request(user.getId(), requesterId);
                } else if("remove".equals(action)) {
                    commDao.delete_friend_request(requesterId, user.getId());
                } else if("sendRequest".equals(action)) {
                    commDao.send_friend_request(user.getId(), requesterId,"");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid userId: " + userIdParam);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
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
