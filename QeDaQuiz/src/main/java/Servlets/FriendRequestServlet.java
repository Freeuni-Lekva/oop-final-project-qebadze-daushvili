package Servlets;

import AccountManager.Account;
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

@WebServlet("/FriendRequestServlet")
public class FriendRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        Account currentUser = (Account) req.getSession().getAttribute("user");
        if (currentUser == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        // Get DAOs
        CommunicationDao commDao = (CommunicationDao) getServletContext().getAttribute("commDao");
        UsersDao usersDao = (UsersDao) getServletContext().getAttribute("accountDB");

        if (commDao == null || usersDao == null) {
            res.sendRedirect("MainPageServlet");
            return;
        }

        String action = req.getParameter("action");
        String senderIdParam = req.getParameter("senderId");

        if (action == null || senderIdParam == null) {
            res.sendRedirect("MessageServlet?action=inbox");
            return;
        }

        try {
            int senderId = Integer.parseInt(senderIdParam);
            Account sender = usersDao.getUser(senderId);

            if (sender == null) {
                req.getSession().setAttribute("errorMessage", "User not found");
                res.sendRedirect("MessageServlet?action=inbox");
                return;
            }

            String successMessage = "";
            String errorMessage = "";

            switch (action) {
                case "accept":
                    // Accept the friend request
                    commDao.accept_request(currentUser.getId(), senderId);
                    successMessage = "Friend request from " + sender.getUsername() + " accepted! You are now friends.";
                    break;

                case "decline":
                    // Decline the friend request
                    commDao.decline_friend_request(currentUser.getId(), senderId);
                    successMessage = "Friend request from " + sender.getUsername() + " declined.";
                    break;

                default:
                    errorMessage = "Invalid action";
                    break;
            }

            // Set success or error message
            if (!successMessage.isEmpty()) {
                req.getSession().setAttribute("successMessage", successMessage);
            } else if (!errorMessage.isEmpty()) {
                req.getSession().setAttribute("errorMessage", errorMessage);
            }

            // Redirect back to inbox
            res.sendRedirect("MessageServlet?action=inbox");

        } catch (NumberFormatException e) {
            req.getSession().setAttribute("errorMessage", "Invalid user ID");
            res.sendRedirect("MessageServlet?action=inbox");
        } catch (SQLException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "Database error occurred");
            res.sendRedirect("MessageServlet?action=inbox");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            req.getSession().setAttribute("errorMessage", "System error occurred");
            res.sendRedirect("MessageServlet?action=inbox");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Redirect GET requests to POST
        doPost(req, res);
    }
}