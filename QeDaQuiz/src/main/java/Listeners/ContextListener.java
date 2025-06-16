package Listeners;

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
        String url = "jdbc:mysql://localhost:3306/skupr23";
        String user = "root";
        String password ="brucewillis";

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
