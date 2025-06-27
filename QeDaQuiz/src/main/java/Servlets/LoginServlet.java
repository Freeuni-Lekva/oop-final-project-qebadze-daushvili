package Servlets;

import AccountManager.Account;
import Daos.UsersDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if(username.equals("") || password.equals("")) {
            req.setAttribute("message", "Fill username and password");
            req.getRequestDispatcher("registration.jsp").forward(req, res);
            return;
        }
        UsersDao db = (UsersDao) getServletContext().getAttribute("accountDB");

        if(db != null) {
            try {
                Account user = db.getUser(username);
                if(user != null) {

                    if(db.checkAccountPassword(username, password)) {
                        req.getSession().setAttribute("user", user);
                        res.sendRedirect("MainPageServlet");
                    }else{
                        req.setAttribute("message", "Wrong password");
                        req.getRequestDispatcher("index.jsp").forward(req, res);
                    }
                }else{
                    req.setAttribute("message", "No such account");
                    req.getRequestDispatcher("index.jsp").forward(req, res);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
