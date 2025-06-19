package Listeners;

import Daos.CommunicationDao;
import Daos.QuizDao;
import Daos.UsersDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String url = "jdbc:mysql://localhost:3306/lkuch23";
        String user = "root";
        String password ="Lizisql2005!";

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UsersDao accounts = null;
        try {
            accounts = new UsersDao(con);
            ServletContext context = sce.getServletContext();
            context.setAttribute("accountDB", accounts);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        QuizDao quizDao = null;
        try {
            quizDao = new QuizDao(con);
            ServletContext context = sce.getServletContext();
            context.setAttribute("quizDao", quizDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        CommunicationDao commDao=null;
        try {
            commDao = new CommunicationDao(con);
            ServletContext context = sce.getServletContext();
            context.setAttribute("commDao", commDao);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
