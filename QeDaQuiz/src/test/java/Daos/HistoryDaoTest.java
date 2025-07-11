package Daos;

import AccountManager.Account;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quiz.history.History;
import quiz.history.Stat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class HistoryDaoTest {
    private static Connection connection;
    private HistoryDao historyDao;
    private QuizDao quizDao;
    private UsersDao usersDao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String url = "jdbc:mysql://localhost:3306/lkuch23";
        String user = "root";
        String password ="Lizisql2005!";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("DROP TABLE IF EXISTS quizes");
        stmt.execute("DROP TABLE IF EXISTS taken_quizes");
        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        stmt.execute("CREATE TABLE users (\n" +
                "    user_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    username VARCHAR(64),\n" +
                "    hashed_password VARCHAR(64),\n" +
                "    image_file VARCHAR(64),\n" +
                "    quizes_made INT DEFAULT 0,\n" +
                "    quizes_taken INT DEFAULT 0\n" +
                ");");
        stmt.execute("CREATE TABLE quizes (\n" +
                "                        quiz_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "                        quiz_name VARCHAR(64),\n" +
                "                        quiz_description VARCHAR(1024),\n" +
                "                        user_id INT,\n" +
                "                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "                        is_random BOOLEAN DEFAULT 0,\n" +
                "                        FOREIGN KEY (user_id) REFERENCES users(user_id)\n" +
                ");");
        stmt.execute("CREATE TABLE taken_quizes (\n" +
                "    taken_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    quiz_id INT,\n" +
                "    user_id INT,\n" +
                "    score INT,\n" +
                "    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id),\n" +
                "    FOREIGN KEY (user_id) REFERENCES users(user_id),\n" +
                "    UNIQUE (quiz_id, user_id)\n" +
                ");");
        stmt.execute("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "type VARCHAR(64)," +
                "prompt VARCHAR(1024) )");
    }

    @Before
    public void setUp() throws Exception {
        quizDao=new QuizDao(connection);
        usersDao=new UsersDao(connection);
        historyDao=new HistoryDao(connection, quizDao, usersDao);
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

        stmt.execute("DELETE FROM taken_quizes");
        stmt.execute("DELETE FROM questions");
        stmt.execute("DELETE FROM quizes");
        stmt.execute("DELETE FROM users");

        stmt.execute("ALTER TABLE users AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE quizes AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE taken_quizes AUTO_INCREMENT = 1");

        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('mockUser1', 'mockPassword1', 'mockImage1');");
        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('mockUser2', 'mockPassword2', 'mockImage2');");

        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id, is_random) VALUES ('mock1', 'mockdesctiption1', 1, false);");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id, is_random) VALUES ('mock2', 'mockdesctiption2', 2, false);");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id, is_random) VALUES ('mock3', 'mockdesctiption3', 1, false);");

        stmt.execute("INSERT INTO taken_quizes (quiz_id, user_id, score) VALUES (1,1,5)");
        stmt.execute("INSERT INTO taken_quizes (quiz_id, user_id, score) VALUES (2,2,6)");
        stmt.execute("INSERT INTO taken_quizes (quiz_id, user_id, score) VALUES (3,1,7)");
    }

    @Test
    public void testUserHistory() throws SQLException {
        History hist1=historyDao.getUserHistory(1);
        History hist2=historyDao.getUserHistory(2);
        assertEquals(2, hist1.getSize());
        assertEquals(1, hist2.getSize());
        assertEquals(hist1.getQuiz(1).getQuizName(), "mock3");
        assertEquals(hist1.getQuiz(2).getQuizDescription(), "mockdesctiption1");
        assertEquals(hist2.getQuiz(1).getQuizName(), "mock2");
        assertEquals(hist2.getQuiz(1).getGottenScore(), 6);

        ArrayList<Account> history1=historyDao.getQuizHistory(1);
        ArrayList<Account> history2=historyDao.getQuizHistory(2);
        assertEquals(1, history1.size());
        assertEquals(history1.get(0).getUsername(), "mockUser1");
        assertEquals(history2.get(0).getUsername(), "mockUser2");
        assertEquals(history2.get(0).getQuizScore(), 6);
    }

    @Test
    public void testGetUserCreatingHistory() throws SQLException {
        History hist1 = historyDao.getUserCreatingHistory(1);
        History hist2 = historyDao.getUserCreatingHistory(2);

        assertEquals(2, hist1.getSize());
        assertEquals(1, hist2.getSize());
    }

    @Test
    public void testGetQuizStats() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE quizes ADD COLUMN max_score INT DEFAULT 0");
        stmt.execute("ALTER TABLE quizes ADD COLUMN average_score FLOAT DEFAULT 0");
        stmt.execute("ALTER TABLE quizes ADD COLUMN average_time FLOAT DEFAULT 0");
        stmt.execute("ALTER TABLE quizes ADD COLUMN taken_by INT DEFAULT 0");
        stmt.execute("ALTER TABLE taken_quizes ADD COLUMN finished_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

        stmt.execute("UPDATE quizes SET max_score = 10, average_score = 6.0, average_time = 300, taken_by = 2 WHERE quiz_id = 1");
        stmt.execute("UPDATE taken_quizes SET finished_at = DATE_ADD(taken_at, INTERVAL 5 MINUTE) WHERE quiz_id = 1");

        List<Stat> stats = historyDao.getQuizStats(1, false);
        assertEquals(1, stats.size());
        assertEquals(5, stats.get(0).getPoints());
        assertEquals(0, stats.get(0).getMaxPoints());
    }

    @Test
    public void testGetQuizStatsByUser() throws SQLException {
        List<Stat> stats = historyDao.getQuizStatsByUser(1, 1);
        assertEquals(1, stats.size());
        assertEquals(1, stats.get(0).getUserId());
        assertEquals(5, stats.get(0).getPoints());
        List<Stat> allStats = historyDao.getQuizStatsByUser(1);
        assertEquals(2, allStats.size());
    }

}
