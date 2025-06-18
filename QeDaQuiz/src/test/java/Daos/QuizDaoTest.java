package Daos;
import org.junit.*;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class QuizDaoTest {
    private static Connection connection;
    private QuizDao quizDao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String url = "jdbc:mysql://localhost:3306/mgior23";
        String user = "root";
        String password ="";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement stmt = connection.createStatement();

        // Drop tables in reverse dependency order
        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("DROP TABLE IF EXISTS answers");
        stmt.execute("DROP TABLE IF EXISTS questions");

        stmt.execute("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(64)," +
                "hashed_password VARCHAR(64)," +
                "image_file VARCHAR(64))");

        stmt.execute("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "type VARCHAR(64)," +
                "prompt VARCHAR(1024))");

        stmt.execute("CREATE TABLE answers (" +
                "answer_id INT AUTO_INCREMENT PRIMARY KEY," +
                "question_id INT," +
                "answer VARCHAR(1024)," +
                "is_correct BOOLEAN," +
                "FOREIGN KEY (question_id) REFERENCES questions(question_id))");

    }

    @Before
    public void setUp() throws Exception {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM users");
        stmt.execute("DELETE FROM answers");
        stmt.execute("DELETE FROM questions");

        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('testUser', 'abc123', 'image.png')", Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        int userId = rs.getInt(1);

        stmt.execute("INSERT INTO questions (question_id, quiz_id, type, prompt) VALUES " +
                "(1, 1, 'RESPONSE_QUESTION', 'What is 2+2?')," +
                "(2, 1, 'MULTIPLE_CHOICE', 'Capital of France?')");

        stmt.execute("INSERT INTO answers (question_id, answer, is_correct) VALUES " +
                "(1, '4', TRUE)," +
                "(1, '3', FALSE)," +
                "(2, 'Paris', TRUE)," +
                "(2, 'London', FALSE)," +
                "(2, 'Berlin', FALSE)");

        quizDao = new QuizDao(connection);
    }

    @Test
    public void testFindAllCorrectAnswers() throws Exception {
        List<String> correctAnswers = quizDao.findAllCorrectAnswers(1);
        assertEquals(1, correctAnswers.size());
        assertEquals("4", correctAnswers.get(0));
    }

    @Test
    public void testFindAllWrongAnswers() throws Exception {
        List<String> wrongAnswers = quizDao.findAllWrongAnswers(2);
        assertEquals(2, wrongAnswers.size());
        assertTrue(wrongAnswers.contains("London"));
        assertTrue(wrongAnswers.contains("Berlin"));
    }

    @Test
    public void testGetQuizQuestions() throws Exception {
        List<Question> questions = quizDao.getQuizQuestions(1);
        assertEquals(2, questions.size());

        Field promptField = Question.class.getDeclaredField("qustion");
        Field typeField = Question.class.getDeclaredField("type");
        Field correctField = Question.class.getDeclaredField("correct_answers");
        promptField.setAccessible(true);
        typeField.setAccessible(true);
        correctField.setAccessible(true);

        Question q1 = questions.get(0);
        assertTrue(q1 instanceof ResponseQuestion);
        assertEquals("What is 2+2?", promptField.get(q1));
        assertEquals("RESPONSE_QUESTION", typeField.get(q1));
        assertTrue(((List<String>) correctField.get(q1)).contains("4"));

        Question q2 = questions.get(1);
        assertTrue(q2 instanceof MultipleChoiceQuestion);
        assertEquals("Capital of France?", promptField.get(q2));
        assertEquals("MULTIPLE_CHOICE", typeField.get(q2));
        assertTrue(((List<String>) correctField.get(q2)).contains("Paris"));

        Field wrongField = MultipleChoiceQuestion.class.getDeclaredField("wrong_answers");
        wrongField.setAccessible(true);
        List<String> wrongs = (List<String>) wrongField.get(q2);
        assertTrue(wrongs.contains("London"));
        assertTrue(wrongs.contains("Berlin"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connection.close();
    }
}
