package Servlets;

import AccountManager.Account;
import Daos.AdminDao;
import Daos.CommunicationDao;
import Daos.UsersDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/AdminPageServlet")

public class AdminPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Account user = (Account) req.getSession().getAttribute("user");
        if (user == null) {
            res.sendRedirect("index.jsp");
            return;
        }
        UsersDao userDao=(UsersDao)getServletContext().getAttribute("accountDB");
        AdminDao adminDao=(AdminDao)getServletContext().getAttribute("adminDao");
        if (adminDao == null || userDao==null) {
            res.sendRedirect("index.jsp");
            return;
        }
        Integer total_quizes_taken=0;
        Integer total_users=0;
        ArrayList<String> announcements=null;
        ArrayList<Account> users=null;
        try {
            announcements=userDao.getAnnouncements();
            total_users=adminDao.getUserNumber();
            total_quizes_taken=adminDao.getTakenQuizNumber();
            String searchQuery = req.getParameter("searchQuery");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                users = adminDao.getAllUsers(searchQuery.trim());
            } else {
                users = adminDao.getAllUsers("");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("announcements",announcements);
        req.setAttribute("total_users",total_users);
        req.setAttribute("total_quizes_taken",total_quizes_taken);
        req.setAttribute("users", users);
        req.getRequestDispatcher("adminPage.jsp").forward(req, res);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Account user = (Account) req.getSession().getAttribute("user");
        if (user == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        UsersDao userDao = (UsersDao) getServletContext().getAttribute("accountDB");
        AdminDao adminDao = (AdminDao) getServletContext().getAttribute("adminDao");

        if (adminDao == null || userDao == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        String action = req.getParameter("action");

        if (action != null) {
            try {
                if (action.equals("addAnnouncement")) {
                    String announcementText = req.getParameter("announcementText");
                    if (announcementText != null && !announcementText.trim().isEmpty()) {
                        adminDao.makeAnnouncement(user.getId(), announcementText);
                    }
                }
                else if (action.equals("removeUser")) {
                    String userId = req.getParameter("userId");
                    if (userId != null && !userId.trim().isEmpty()) {
                        adminDao.removeUser(Integer.parseInt(userId));
                    }
                }
                else if (action.equals("makeAdmin")) {
                    String userId = req.getParameter("userId");
                    if (userId != null && !userId.trim().isEmpty()) {
                        adminDao.makeAdmin(Integer.parseInt(userId));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        doGet(req, res);
    }
}
