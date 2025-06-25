package Daos;
import org.junit.*;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;
import quiz.quiz.Quiz;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class QuizDaoTest {
    private static Connection connection;
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
        stmt.execute("DROP TABLE IF EXISTS answers");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("DROP TABLE IF EXISTS quizes");
        stmt.execute("DROP TABLE IF EXISTS friend_requests");
        stmt.execute("DROP TABLE IF EXISTS messages");
        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("DROP TABLE IF EXISTS answers");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        stmt.execute("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(64)," +
                "hashed_password VARCHAR(64)," +
                "image_file VARCHAR(64)," +
                "quizes_made INT DEFAULT 0," +
                "quizes_taken INT DEFAULT 0)");

        stmt.execute("CREATE TABLE quizes (" +
                "quiz_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_name VARCHAR(64)," +
                "quiz_description VARCHAR(1024)," +
                "user_id INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "max_score INT DEFAULT 0," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");

        stmt.execute("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "type VARCHAR(64)," +
                "prompt VARCHAR(1024) )");

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

        stmt.execute("DELETE FROM answers");
        stmt.execute("DELETE FROM questions");
        stmt.execute("DELETE FROM taken_quizes");
        stmt.execute("DELETE FROM quizes");
        stmt.execute("DELETE FROM users");

        stmt.execute("ALTER TABLE answers AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE questions AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE taken_quizes AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE quizes AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE users AUTO_INCREMENT = 1");

        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('testUser', 'abc123', 'image.png')", Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        int userId = rs.getInt(1);

        stmt.execute("INSERT INTO quizes (quiz_id, quiz_name, quiz_description, user_id) VALUES (1, 'Math Quiz', 'Simple math questions', " + userId + ")");

        stmt.execute("INSERT INTO questions (question_id, quiz_id, type, prompt) VALUES " +
                "(1, 1, 'RESPONSE_QUESTION', 'What is 2+2?')," +
                "(2, 1, 'MULTIPLE_CHOICE', 'Capital of France?')");

        stmt.execute("INSERT INTO answers (question_id, answer, is_correct) VALUES " +
                "(1, '4', TRUE)," +
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
    public void testAddQuizIndependently() throws Exception {
        // Create a new user for isolation
        PreparedStatement psUser = connection.prepareStatement(
                "INSERT INTO users (username, hashed_password, image_file) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, "isolatedUser");
        psUser.setString(2, "password123");
        psUser.setString(3, "image2.png");
        psUser.executeUpdate();
        ResultSet rsUser = psUser.getGeneratedKeys();
        rsUser.next();
        int newUserId = rsUser.getInt(1);

        List<String> correct1 = Arrays.asList("42");
        ResponseQuestion q1 = new ResponseQuestion("What is the answer to life?", correct1, "RESPONSE_QUESTION");

        List<String> correct2 = Arrays.asList("Jupiter");
        List<String> wrong2 = Arrays.asList("Mars", "Saturn");
        MultipleChoiceQuestion q2 = new MultipleChoiceQuestion("Largest planet?", correct2, wrong2, "MULTIPLE_CHOICE");

        List<Question> questions = new ArrayList<>();
        questions.add(q1);
        questions.add(q2);

        Quiz quiz = new Quiz(0, "Science Quiz", "A quiz about science", newUserId, questions);

        quizDao.addQuiz(quiz);

        Quiz stored = quizDao.getQuiz(quiz.getQuizId());

        assertNotNull(stored);
        assertEquals("Science Quiz", stored.getQuizName());
        assertEquals("A quiz about science", stored.getQuizDescription());
        assertEquals(newUserId, stored.getUserId());
        assertEquals(2, stored.getQuestions().size());
    }

    @Test
    public void testRemoveQuestion() throws Exception {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO questions (quiz_id, type, prompt) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, 1);
        ps.setString(2, "RESPONSE_QUESTION");
        ps.setString(3, "Temporary question?");
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int questionId = rs.getInt(1);

        List<Question> questionsBefore = quizDao.getQuizQuestions(1);
        boolean existsBefore = questionsBefore.stream().anyMatch(q -> {
            try {
                Field promptField = Question.class.getDeclaredField("question");
                promptField.setAccessible(true);
                return promptField.get(q).equals("Temporary question?");
            } catch (Exception e) {
                return false;
            }
        });
        assertTrue(existsBefore);

        quizDao.removeQuestion(questionId);

        List<Question> questionsAfter = quizDao.getQuizQuestions(1);
        boolean existsAfter = questionsAfter.stream().anyMatch(q -> {
            try {
                Field promptField = Question.class.getDeclaredField("question");
                promptField.setAccessible(true);
                return promptField.get(q).equals("Temporary question?");
            } catch (Exception e) {
                return false;
            }
        });
        assertFalse(existsAfter);
    }

    @Test
    public void testRemoveQuiz() throws Exception {
        PreparedStatement psUser = connection.prepareStatement(
                "INSERT INTO users (username, hashed_password, image_file) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, "tempUser2");
        psUser.setString(2, "pass123");
        psUser.setString(3, "temp2.png");
        psUser.executeUpdate();
        ResultSet rsUser = psUser.getGeneratedKeys();
        rsUser.next();
        int newUserId = rsUser.getInt(1);

        List<Question> questions = new ArrayList<>();
        Quiz quiz = new Quiz(0, "Temp Quiz to Remove", "Temp Description", newUserId, questions);
        quizDao.addQuiz(quiz);
        int quizId = quiz.getQuizId();

        Quiz stored = quizDao.getQuiz(quizId);
        assertNotNull("Quiz should exist before removal", stored);
        assertEquals("Quiz should have 0 questions before removal", 0, stored.getQuestions().size());

        quizDao.removeQuiz(quizId);

        Quiz removedQuiz = quizDao.getQuiz(quizId);
        assertNull("Quiz should be null after removal", removedQuiz);
    }

    @Test
    public void testAddAndGetQuiz() throws Exception {
        int userId = 1;

        List<String> correct1 = new ArrayList<String>();
        correct1.add("4");
        ResponseQuestion q1 = new ResponseQuestion("What is 2+2?", correct1, "RESPONSE_QUESTION");

        List<String> correct2 = new ArrayList<String>();
        correct2.add("Berlin");
        List<String> wrong2 = new ArrayList<String>();
        wrong2.add("Paris");
        wrong2.add("Madrid");
        MultipleChoiceQuestion q2 = new MultipleChoiceQuestion("Capital of Germany?", correct2, wrong2, "MULTIPLE_CHOICE");

        List<Question> questions = new ArrayList<Question>();
        questions.add(q1);
        questions.add(q2);

        Quiz quiz = new Quiz(0, "New Quiz", "A quiz about capitals and math", userId, questions);

        quizDao.addQuiz(quiz);

        Quiz stored = quizDao.getQuiz(quiz.getQuizId());

        assertNotNull(stored);
        assertEquals("New Quiz", stored.getQuizName());
        assertEquals("A quiz about capitals and math", stored.getQuizDescription());
        assertEquals(userId, stored.getUserId());
        assertEquals(2, stored.getQuestions().size());
    }


    @Test
    public void testGetQuizQuestions() throws Exception {
        List<Question> questions = quizDao.getQuizQuestions(1);
        assertEquals(2, questions.size());

        Field promptField = Question.class.getDeclaredField("question");
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
