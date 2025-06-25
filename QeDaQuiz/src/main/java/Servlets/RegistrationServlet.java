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

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("username");
        String password = req.getParameter("password");
        String picture = req.getParameter("picture");
        if(name.equals("") || password.equals("")) {
            req.setAttribute("message", "Fill username and password");
            req.getRequestDispatcher("registration.jsp").forward(req, res);
            return;
        }
        UsersDao db = (UsersDao) getServletContext().getAttribute("accountDB");

        try {
            if(db.checkAccountName(name)){
                req.setAttribute("message", "Account already exists");
                req.getRequestDispatcher("registration.jsp").forward(req, res);
            }else{
                Account account = new Account(password, name, picture);
                db.addAccount(account);
                req.setAttribute("user", account);
                req.getRequestDispatcher("mainPage.jsp").forward(req, res);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
