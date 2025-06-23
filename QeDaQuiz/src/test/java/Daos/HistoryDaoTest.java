package Daos;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import quiz.history.History;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;


public class HistoryDaoTest {
    private static Connection connection;
    private HistoryDao historyDao;
    private QuizDao quizDao;
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
        stmt.execute("DROP TABLE IF EXISTS quizes");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        stmt.execute("CREATE TABLE quizes (\n" +
                "                        quiz_id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "                        quiz_name VARCHAR(64),\n" +
                "                        quiz_description VARCHAR(1024),\n" +
                "                        user_id INT,\n" +
                "                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "                        FOREIGN KEY (user_id) REFERENCES users(user_id)\n" +
                ");");
        stmt.execute("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "type VARCHAR(64)," +
                "prompt VARCHAR(1024), " +
                "FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id)" +
                ");");
    }
    @Before
    public void setUp() throws Exception {
        quizDao=new QuizDao(connection);
        historyDao=new HistoryDao(connection, quizDao);
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES ('mock1', 'mockdesctiption1', 1);");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES ('mock2', 'mockdesctiption2', 2);");
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES ('mock3', 'mockdesctiption3', 1);");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
    @Test
    public void testHistory() throws SQLException {
        History hist1=historyDao.addHistory(1);
        History hist2=historyDao.addHistory(2);
        assertEquals(2, hist1.getSize());
        assertEquals(1, hist2.getSize());
        assertEquals(hist1.getQuiz(1).getQuizName(), "mock3");
        assertEquals(hist1.getQuiz(2).getQuizDescription(), "mockdesctiption1");
        assertEquals(hist2.getQuiz(1).getQuizName(), "mock2");
    }
}
