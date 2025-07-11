package Daos;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdminDaoTest {
    private static Connection connection;
    private AdminDao adminDao;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String url = "jdbc:mysql://localhost:3306/lkuch23";
        String user = "root";
        String password ="Lizisql2005!";

        connection = DriverManager.getConnection(url, user, password);
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        stmt.execute("DROP TABLE IF EXISTS taken_quizes");
        stmt.execute("DROP TABLE IF EXISTS announcements");
        stmt.execute("DROP TABLE IF EXISTS quizes");
        stmt.execute("DROP TABLE IF EXISTS users");

        stmt.execute("CREATE TABLE users (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    username VARCHAR(64),\n" +
                "    hashed_password VARCHAR(64),\n" +
                "    image_file VARCHAR(64),\n" +
                "    quizes_made INT DEFAULT 0,\n" +
                "    quizes_taken INT DEFAULT 0,\n" +
                "    is_admin BOOLEAN DEFAULT 0\n" +
                ");");

        stmt.execute("CREATE TABLE quizes (\n" +
                "    quiz_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    quiz_name VARCHAR(64),\n" +
                "    quiz_description VARCHAR(1024),\n" +
                "    user_id INT,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    max_score INT DEFAULT 0,\n" +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE\n" +
                ");");

        stmt.execute("CREATE TABLE taken_quizes (\n" +
                "    taken_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    quiz_id INT,\n" +
                "    user_id INT,\n" +
                "    score INT,\n" +
                "    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,\n" +
                "    UNIQUE (quiz_id, user_id)\n" +
                ");");

        stmt.execute("CREATE TABLE announcements (\n" +
                "    announcement_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    admin_user_id INT,\n" +
                "    content TEXT,\n" +
                "    made_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE\n" +
                ");");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
    @Before
    public void setUp() throws SQLException {
        adminDao = new AdminDao(connection);

        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

        stmt.execute("DELETE FROM announcements");
        stmt.execute("DELETE FROM taken_quizes");
        stmt.execute("DELETE FROM quizes");
        stmt.execute("DELETE FROM users");

        stmt.execute("ALTER TABLE users AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE quizes AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE taken_quizes AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE announcements AUTO_INCREMENT = 1");

        stmt.execute("INSERT INTO users (username, hashed_password, image_file, is_admin) VALUES ('admin1', 'pass', 'img', 1)");
        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('user1', 'pass', 'img')");
        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('user2', 'pass', 'img')");

        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES ('quiz1', 'desc1', 1)");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES ('quiz2', 'desc2', 2)");

        stmt.execute("INSERT INTO taken_quizes (quiz_id, user_id, score) VALUES (1, 1, 10)");
        stmt.execute("INSERT INTO taken_quizes (quiz_id, user_id, score) VALUES (2, 2, 15)");

        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        if (!connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    @Test
    public void testMakeAnnouncement() throws SQLException {
        adminDao.makeAnnouncement(1, "Site maintenance");
        PreparedStatement ps = connection.prepareStatement("SELECT content FROM announcements WHERE admin_user_id = 1");
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertEquals("Site maintenance", rs.getString(1));
    }

    @Test
    public void testRemoveUser() throws SQLException {
        adminDao.removeUser(2);
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE user_id = 2");
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void testRemoveQuiz() throws SQLException {
        adminDao.removeQuiz(1);
        PreparedStatement ps1 = connection.prepareStatement("SELECT COUNT(*) FROM taken_quizes WHERE quiz_id = 1");
        ResultSet rs1 = ps1.executeQuery();
        rs1.next();
        assertEquals(0, rs1.getInt(1));

        PreparedStatement ps2 = connection.prepareStatement("SELECT COUNT(*) FROM quizes WHERE quiz_id = 1");
        ResultSet rs2 = ps2.executeQuery();
        rs2.next();
        assertEquals(0, rs2.getInt(1));
    }

    @Test
    public void testRemoveQuizHistory() throws SQLException {
        adminDao.removeQuizHistory(2);
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM taken_quizes WHERE quiz_id = 2");
        ResultSet rs = ps.executeQuery();
        rs.next();
        assertEquals(0, rs.getInt(1));
    }

    @Test
    public void testMakeAdmin() throws SQLException {
        adminDao.makeAdmin(2);
        PreparedStatement ps = connection.prepareStatement("SELECT is_admin FROM users WHERE user_id = 2");
        ResultSet rs = ps.executeQuery();
        rs.next();

        assertEquals(true, rs.getBoolean(1));
        assertTrue(adminDao.isAdmin(2));
    }

    @Test
    public void testGetUserNumber() throws SQLException {
        assertEquals(3, adminDao.getUserNumber());
    }

    @Test
    public void testGetTakenQuizNumber() throws SQLException {
        assertEquals(2, adminDao.getTakenQuizNumber());
    }

    @Test
    public void testGetAllUsers() throws SQLException, NoSuchAlgorithmException {
        assertEquals(2,adminDao.getAllUsers("user").size());
    }
}

