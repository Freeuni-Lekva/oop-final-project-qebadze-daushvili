package Daos;
import AccountManager.PasswordHasher;
import Constantas.Constantas;
import Daos.UsersDao;
import AccountManager.Account;
import org.junit.*;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;
import quiz.quiz.Quiz;

import java.sql.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class UsersDaoTest {

    private static Connection connection;
    private UsersDao usersDao;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String url = "jdbc:mysql://localhost:3306/lkuch23";
        String user = "root";
        String password = "Lizisql2005!";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        stmt.execute("DROP TABLE IF EXISTS taken_quizes");
        stmt.execute("DROP TABLE IF EXISTS achievements");
        stmt.execute("DROP TABLE IF EXISTS messages");
        stmt.execute("DROP TABLE IF EXISTS friend_requests");
        stmt.execute("DROP TABLE IF EXISTS answers");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("DROP TABLE IF EXISTS quizes");
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
                "quiz_name VARCHAR(264)," +
                "quiz_description VARCHAR(1024)," +
                "user_id INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "max_score INT DEFAULT 0," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")");
        stmt.execute("CREATE TABLE achievements (" +
                "achievement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT," +
                "achievement VARCHAR(64)," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)," +
                "UNIQUE (user_id, achievement)" +
                ")");
        stmt.execute("CREATE TABLE taken_quizes (" +
                "taken_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "user_id INT," +
                "score INT," +
                "taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id)," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")");
    }

    @Before
    public void setUp() throws Exception {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM taken_quizes");
        stmt.execute("DELETE FROM achievements");
        stmt.execute("DELETE FROM quizes");
        stmt.execute("DELETE FROM users");


        usersDao = new UsersDao(connection);

        Account john = new Account("pass123", "john", "pic.jpg");
        usersDao.addAccount(john);
    }

    @Test
    public void testCheckAccountPasswordCorrect() throws Exception {
        assertTrue(usersDao.checkAccountPassword("john", "pass123"));
    }

    @Test
    public void testCheckAccountPasswordWrong() throws Exception {
        assertFalse(usersDao.checkAccountPassword("john", "wrongpass"));
    }

    @Test
    public void testCheckAccountPasswordNonexistentUser() throws Exception {
        assertFalse(usersDao.checkAccountPassword("alice", "pass123"));
    }

    @Test
    public void testAddAccountAndLogin() throws Exception {
        Account alice = new Account("secret", "alice", "avatar.jpg");
        usersDao.addAccount(alice);

        assertTrue(usersDao.checkAccountName("alice"));
        assertTrue(usersDao.checkAccountPassword("alice", "secret"));
    }

    @Test
    public void testAddAccountDuplicateIgnored() throws Exception {
        Account dupe = new Account("whatever", "john", "pic2.jpg");
        usersDao.addAccount(dupe); // Should do nothing

        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
        ps.setString(1, "john");
        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        assertEquals(1, count);
    }

    @Test
    public void testCheckAccountName() throws Exception {
        assertTrue(usersDao.checkAccountName("john"));
        assertFalse(usersDao.checkAccountName("ghost"));
    }

    @Test
    public void testGetUser() throws SQLException, NoSuchAlgorithmException {
        Account Matikuna = new Account("mate13012006", "dambo", "melondonshi.jpg");
        usersDao.addAccount(Matikuna);
        int matikunaId = Matikuna.getId();
        Account acc = usersDao.getUser(matikunaId);
        assertNotNull(acc);
        assertEquals("dambo", acc.getUsername());
        assertEquals("melondonshi.jpg", acc.getPhoto());
        assertEquals(Matikuna.getPassword(), acc.getPassword());
    }

    @Test
    public void testTakeQuiz() throws Exception {
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
        assertFalse(usersDao.hasAchievement(newUserId,"Practice Makes Perfect"));

        PreparedStatement psUser2 = connection.prepareStatement(
                "INSERT INTO users (username, hashed_password, image_file) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        psUser2.setString(1, "isolatedUser2");
        psUser2.setString(2, "password123");
        psUser2.setString(3, "image2.png");
        psUser2.executeUpdate();
        ResultSet rsUser2 = psUser2.getGeneratedKeys();
        rsUser2.next();
        int newUserId2 = rsUser2.getInt(1);

        List<String> correct1 = Arrays.asList("42");
        ResponseQuestion q1 = new ResponseQuestion("What is the answer to life?", correct1, "RESPONSE_QUESTION");
        List<String> correct2 = Arrays.asList("Jupiter");
        List<String> wrong2 = Arrays.asList("Mars", "Saturn");
        MultipleChoiceQuestion q2 = new MultipleChoiceQuestion("Largest planet?", correct2, wrong2, "MULTIPLE_CHOICE");
        List<Question> questions = new ArrayList<>();
        questions.add(q1);
        questions.add(q2);
        Quiz quiz1 = new Quiz(0, "Science Quiz", "A quiz about science", newUserId, questions);

        String insertQuizSql = "INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertQuizSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz1.getQuizName());
            ps.setString(2, quiz1.getQuizDescription());
            ps.setInt(3, newUserId); // or any valid user_id
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int quizId = rs.getInt(1);
            assertEquals(0, usersDao.getTakenQuizesQuantity(newUserId));
            assertFalse(usersDao.hasAchievement(newUserId,"I am the Greatest"));
            usersDao.takeQuiz(newUserId, quizId, 3);
            assertEquals(1, usersDao.getTakenQuizesQuantity(newUserId));
            assertTrue(usersDao.hasAchievement(newUserId,"I am the Greatest"));
            usersDao.takeQuiz(newUserId, quizId, 2);
            assertEquals(2, usersDao.getTakenQuizesQuantity(newUserId));
            assertFalse(usersDao.hasAchievement(newUserId,"Quiz Machine"));
            for (int i = 0; i < Constantas.QUIZ_MACHINE_QUIZES_TAKEN - 2; i++) {
                usersDao.takeQuiz(newUserId, quizId, 2);
            }
            assertTrue(usersDao.hasAchievement(newUserId,"Quiz Machine"));
            usersDao.takeQuiz(newUserId2, quizId, 2);
            assertFalse(usersDao.hasAchievement(newUserId2,"I am the Greatest"));
            usersDao.takeQuiz(newUserId2, quizId, 7);
            assertTrue(usersDao.hasAchievement(newUserId2,"I am the Greatest"));
            assertTrue(usersDao.hasAchievement(newUserId,"I am the Greatest"));

            usersDao.takeQuizInPracticeMode(newUserId, quizId);
            assertTrue(usersDao.hasAchievement(newUserId,"Practice Makes Perfect"));

        }

    }

    @Test
    public void testMakeQuiz() throws Exception {
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
        assertEquals(0, usersDao.getMadeQuizesQuantity(newUserId));

        List<String> correct1 = Arrays.asList("42");
        ResponseQuestion q1 = new ResponseQuestion("What is the answer to life?", correct1, "RESPONSE_QUESTION");
        List<String> correct2 = Arrays.asList("Jupiter");
        List<String> wrong2 = Arrays.asList("Mars", "Saturn");
        MultipleChoiceQuestion q2 = new MultipleChoiceQuestion("Largest planet?", correct2, wrong2, "MULTIPLE_CHOICE");
        List<Question> questions = new ArrayList<>();
        questions.add(q1);
        questions.add(q2);
        Quiz quiz1 = new Quiz(0, "Science Quiz", "A quiz about science", newUserId, questions);
        assertFalse(usersDao.hasAchievement(newUserId, "AMATEUR AUTHOR"));
        usersDao.makeQuiz(quiz1);
        assertEquals(1, usersDao.getMadeQuizesQuantity(newUserId));
        assertTrue(usersDao.hasAchievement(newUserId, "AMATEUR AUTHOR"));
        assertFalse(usersDao.hasAchievement(newUserId, "PROLIFIC AUTHOR"));

        List<String> correct3 = Arrays.asList("Zaali");
        List<String> wrong3 = Arrays.asList("Amirani", "Shota");
        MultipleChoiceQuestion q3 = new MultipleChoiceQuestion("Vin aswavlis litxels?", correct3, wrong3, "MULTIPLE_CHOICE");
        List<Question> questions2 = new ArrayList<>();
        questions2.add(q3);
        Quiz quiz2 = new Quiz(1, "Zogadebi", "A quiz about zogadebi", newUserId, questions2);
        Quiz quiz3 = new Quiz(2, "Freeuni", "A quiz about freeuni", newUserId, questions2);
        Quiz quiz4 = new Quiz(3, "Uni", "A quiz about uni", newUserId, questions2);
        Quiz quiz5 = new Quiz(4, "Campus", "A quiz about campus", newUserId, questions2);
        usersDao.makeQuiz(quiz2);
        usersDao.makeQuiz(quiz3);
        usersDao.makeQuiz(quiz4);
        usersDao.makeQuiz(quiz5);
        assertTrue(usersDao.hasAchievement(newUserId, "AMATEUR AUTHOR"));
        assertTrue(usersDao.hasAchievement(newUserId, "PROLIFIC AUTHOR"));
        assertFalse(usersDao.hasAchievement(newUserId, "PRODIGIOUS AUTHOR"));
        assertEquals(Constantas.PROLIFIC_AUTHOR_QUIZES_MADE, usersDao.getMadeQuizesQuantity(newUserId));

        Quiz quiz6 = new Quiz(5, "Science", "A quiz about science", newUserId, questions);
        Quiz quiz7 = new Quiz(6, "Stem", "A quiz about stem", newUserId, questions);
        Quiz quiz8 = new Quiz(7, "Science", "A quiz about science", newUserId, questions);
        Quiz quiz9 = new Quiz(8, "Subjects", "A quiz about subjects", newUserId, questions2);
        Quiz quiz10 = new Quiz(9, "Subject", "A quiz about subjects", newUserId, questions2);
        usersDao.makeQuiz(quiz6);
        usersDao.makeQuiz(quiz7);
        usersDao.makeQuiz(quiz8);
        usersDao.makeQuiz(quiz9);
        usersDao.makeQuiz(quiz10);
        assertTrue(usersDao.hasAchievement(newUserId, "PRODIGIOUS AUTHOR"));
        assertFalse(usersDao.hasAchievement(newUserId, "QUIZ MACHINE"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connection.close();
    }
}
