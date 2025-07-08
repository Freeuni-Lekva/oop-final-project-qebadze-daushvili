package Servlets;

import AccountManager.Account;
import AccountManager.Message;
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

@WebServlet("/MessageServlet")
public class MessageServlet extends HttpServlet {

    private String recipientIdParam;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
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

        recipientIdParam = req.getParameter("recipientId");
        String action = req.getParameter("action");

        try {
            if ("inbox".equals(action)) {
                // Show user's inbox
                ArrayList<Message> receivedMessages = commDao.getAllGottenMessages(currentUser.getId());
                req.setAttribute("receivedMessages", receivedMessages);
                req.getRequestDispatcher("inbox.jsp").forward(req, res);

            } else if ("sent".equals(action)) {
                // Show user's sent messages
                ArrayList<Message> sentMessages = commDao.getAllSentMessages(currentUser.getId());
                req.setAttribute("sentMessages", sentMessages);
                req.getRequestDispatcher("sentMessages.jsp").forward(req, res);

            } else if (recipientIdParam != null) {
                // Show compose message form
                int recipientId = Integer.parseInt(recipientIdParam);
                Account recipient = usersDao.getUser(recipientId);

                if (recipient == null) {
                    req.setAttribute("error", "User not found");
                    req.getRequestDispatcher("error.jsp").forward(req, res);
                    return;
                }

                // Check friend status
                String friendStatus = commDao.check_friends_status(currentUser.getId(), recipientId);

                req.setAttribute("recipient", recipient);
                req.setAttribute("friendStatus", friendStatus);
                req.getRequestDispatcher("composeMessage.jsp").forward(req, res);

            } else {
                // Default: show inbox
                ArrayList<Message> receivedMessages = commDao.getAllGottenMessages(currentUser.getId());
                req.setAttribute("receivedMessages", receivedMessages);
                req.getRequestDispatcher("inbox.jsp").forward(req, res);
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error occurred");
            req.getRequestDispatcher("error.jsp").forward(req, res);
        }
    }

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

        String messageType = req.getParameter("messageType");
        //System.out.println(recipientIdParam);
        String messageContent = req.getParameter("messageContent");
        String quizIdParam = req.getParameter("quizId");

        try {
            int recipientId = Integer.parseInt(recipientIdParam);
            System.out.println(recipientId);
            Account recipient = usersDao.getUser(recipientId);
           // System.out.println(recipient.getId());

            if (recipient == null) {
                req.setAttribute("error", "Recipient not found lol");
                req.getRequestDispatcher("error.jsp").forward(req, res);
                return;
            }

            // Get friend status - IMPORTANT: needed for error handling
            String friendStatus = commDao.check_friends_status(currentUser.getId(), recipientId);

            // Validate message content
            if (messageContent == null || messageContent.trim().isEmpty()) {
                req.setAttribute("error", "Message content cannot be empty");
                req.setAttribute("recipient", recipient);
                req.setAttribute("friendStatus", friendStatus); // FIX: Add this line
                req.getRequestDispatcher("composeMessage.jsp").forward(req, res);
                return;
            }

            // Validate message type
            if (messageType == null || messageType.trim().isEmpty()) {
                req.setAttribute("error", "Please select a message type");
                req.setAttribute("recipient", recipient);
                req.setAttribute("friendStatus", friendStatus); // FIX: Add this line
                req.getRequestDispatcher("composeMessage.jsp").forward(req, res);
                return;
            }

            // Send message based on type
            switch (messageType) {
                case "NOTE":
                    commDao.send_note_message(currentUser.getId(), recipientId, messageContent.trim());
                    break;

                case "FRIEND_REQUEST":
                    commDao.send_friend_request(currentUser.getId(), recipientId, messageContent.trim());
                    break;

                case "CHALLENGE":
                    if (quizIdParam != null && !quizIdParam.trim().isEmpty()) {
                        int quizId = Integer.parseInt(quizIdParam);
                        commDao.send_challenge_message(currentUser.getId(), recipientId, messageContent.trim(), quizId);
                    } else {
                        req.setAttribute("error", "Quiz ID is required for challenge messages");
                        req.setAttribute("recipient", recipient);
                        req.setAttribute("friendStatus", friendStatus); // FIX: Add this line
                        req.getRequestDispatcher("composeMessage.jsp").forward(req, res);
                        return;
                    }
                    break;

                default:
                    req.setAttribute("error", "Invalid message type");
                    req.setAttribute("recipient", recipient);
                    req.setAttribute("friendStatus", friendStatus); // FIX: Add this line
                    req.getRequestDispatcher("composeMessage.jsp").forward(req, res);
                    return;
            }

            // Set success message and redirect
            req.getSession().setAttribute("successMessage", "Message sent successfully to " + recipient.getUsername());
            res.sendRedirect("MessageServlet?action=sent");

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid user ID or quiz ID");
            req.getRequestDispatcher("error.jsp").forward(req, res);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Database error occurred while sending message");
            req.getRequestDispatcher("error.jsp").forward(req, res);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            req.setAttribute("error", "System error occurred");
            req.getRequestDispatcher("error.jsp").forward(req, res);
        }
    }
}