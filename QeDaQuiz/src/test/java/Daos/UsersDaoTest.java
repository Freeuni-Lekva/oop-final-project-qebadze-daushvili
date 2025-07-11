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

        stmt.execute("DROP TABLE IF EXISTS answers");
        stmt.execute("DROP TABLE IF EXISTS questions");
        stmt.execute("DROP TABLE IF EXISTS taken_quizes");
        stmt.execute("DROP TABLE IF EXISTS achievements");
        stmt.execute("DROP TABLE IF EXISTS messages");
        stmt.execute("DROP TABLE IF EXISTS quizes");
        stmt.execute("DROP TABLE IF EXISTS friend_requests");
        stmt.execute("DROP TABLE IF EXISTS announcements");
        stmt.execute("DROP TABLE IF EXISTS users");

        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        stmt.execute("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(1024)," +
                "hashed_password VARCHAR(1024)," +
                "image_file TEXT," +
                "quizes_made INT DEFAULT 0," +
                "quizes_taken INT DEFAULT 0," +
                "is_admin BOOLEAN DEFAULT 0" +
                ")");

        stmt.execute("CREATE TABLE friend_requests (" +
                "request_id INT PRIMARY KEY AUTO_INCREMENT," +
                "from_user_id INT," +
                "to_user_id INT," +
                "status ENUM('PENDING', 'ACCEPTED') DEFAULT 'PENDING'," +
                "FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        stmt.execute("CREATE TABLE quizes (" +
                "quiz_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_name VARCHAR(64)," +
                "quiz_description VARCHAR(1024)," +
                "user_id INT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "max_score INT DEFAULT 0," +
                "taken_by INT DEFAULT 0," +
                "average_score DOUBLE DEFAULT 0," +
                "average_time DOUBLE DEFAULT 0," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        stmt.execute("CREATE TABLE messages (" +
                "message_id INT PRIMARY KEY AUTO_INCREMENT," +
                "from_user_id INT," +
                "to_user_id INT," +
                "type ENUM('FRIEND_REQUEST', 'CHALLENGE', 'NOTE')," +
                "content TEXT," +
                "quiz_id INT," +
                "FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE SET NULL" +
                ")");

        stmt.execute("CREATE TABLE questions (" +
                "question_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "type ENUM('Fill in the Blank','Question-Response','Multiple Choice','Picture-Response')," +
                "prompt VARCHAR(1024)," +
                "FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE CASCADE" +
                ")");

        stmt.execute("CREATE TABLE answers (" +
                "answer_id INT AUTO_INCREMENT PRIMARY KEY," +
                "question_id INT," +
                "answer VARCHAR(1024)," +
                "is_correct BOOLEAN," +
                "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE" +
                ")");

        stmt.execute("CREATE TABLE achievements (" +
                "achievement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT," +
                "achievement VARCHAR(64)," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "UNIQUE (user_id, achievement)" +
                ")");

        stmt.execute("CREATE TABLE taken_quizes (" +
                "taken_id INT AUTO_INCREMENT PRIMARY KEY," +
                "quiz_id INT," +
                "user_id INT," +
                "score INT," +
                "taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "finished_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ")");

        stmt.execute("CREATE TABLE announcements (" +
                "announcement_id INT AUTO_INCREMENT PRIMARY KEY," +
                "admin_user_id INT," +
                "content TEXT," +
                "made_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
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
    public void testGetUserWithName() throws SQLException, NoSuchAlgorithmException {
        Account batman = new Account("bruce", "batman", "batman.jpg");
        usersDao.addAccount(batman);
        Account acc = usersDao.getUser("batman");
        assertNotNull(acc);
        assertEquals("batman", acc.getUsername());
        assertEquals("batman.jpg", acc.getPhoto());
        assertEquals(batman.getPassword(), acc.getPassword());
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
        Quiz quiz1 = new Quiz(0, "Science Quiz", "A quiz about science", newUserId, questions, false);
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
        Quiz quiz2 = new Quiz(1, "Zogadebi", "A quiz about zogadebi", newUserId, questions2, false);
        Quiz quiz3 = new Quiz(2, "Freeuni", "A quiz about freeuni", newUserId, questions2, false);
        Quiz quiz4 = new Quiz(3, "Uni", "A quiz about uni", newUserId, questions2, false);
        Quiz quiz5 = new Quiz(4, "Campus", "A quiz about campus", newUserId, questions2, false);
        usersDao.makeQuiz(quiz2);
        usersDao.makeQuiz(quiz3);
        usersDao.makeQuiz(quiz4);
        usersDao.makeQuiz(quiz5);
        assertTrue(usersDao.hasAchievement(newUserId, "AMATEUR AUTHOR"));
        assertTrue(usersDao.hasAchievement(newUserId, "PROLIFIC AUTHOR"));
        assertFalse(usersDao.hasAchievement(newUserId, "PRODIGIOUS AUTHOR"));
        assertEquals(Constantas.PROLIFIC_AUTHOR_QUIZES_MADE, usersDao.getMadeQuizesQuantity(newUserId));

        Quiz quiz6 = new Quiz(5, "Science", "A quiz about science", newUserId, questions, false);
        Quiz quiz7 = new Quiz(6, "Stem", "A quiz about stem", newUserId, questions, false);
        Quiz quiz8 = new Quiz(7, "Science", "A quiz about science", newUserId, questions, false);
        Quiz quiz9 = new Quiz(8, "Subjects", "A quiz about subjects", newUserId, questions2, false);
        Quiz quiz10 = new Quiz(9, "Subject", "A quiz about subjects", newUserId, questions2, true);
        usersDao.makeQuiz(quiz6);
        usersDao.makeQuiz(quiz7);
        usersDao.makeQuiz(quiz8);
        usersDao.makeQuiz(quiz9);
        usersDao.makeQuiz(quiz10);
        assertTrue(usersDao.hasAchievement(newUserId, "PRODIGIOUS AUTHOR"));
        assertFalse(usersDao.hasAchievement(newUserId, "QUIZ MACHINE"));
        assertTrue(quiz10.isRandom());
    }

    @Test
    public void testSearchUsersByUsername() throws Exception {
        Account alice = new Account("secret", "alice", "avatar.jpg");
        usersDao.addAccount(alice);

        Account john = usersDao.getUser("john");
        int johnId = john.getId();

        ArrayList<Account> results = usersDao.searchUsersByUsername("a", johnId);
        assertEquals(1, results.size());
        assertEquals("alice", results.get(0).getUsername());

        ArrayList<Account> noResults = usersDao.searchUsersByUsername("xyz", johnId);
        assertEquals(0, noResults.size());
    }

    @Test
    public void testGetTakenQuizesQuantity() throws Exception {
        Account john = usersDao.getUser("john");
        assertEquals(0, usersDao.getTakenQuizesQuantity(john.getId()));

        assertEquals(0, usersDao.getTakenQuizesQuantity(99999));
    }

    @Test
    public void testUpdateQuizMaxScore() throws Exception {
        Account john = usersDao.getUser("john");
        int johnId = john.getId();

        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id, max_score) VALUES ('Test Quiz', 'Description', " + johnId + ", 5)");

        ResultSet rs = stmt.executeQuery("SELECT quiz_id FROM quizes WHERE quiz_name = 'Test Quiz'");
        rs.next();
        int quizId = rs.getInt("quiz_id");

        usersDao.updateQuizMaxScore(quizId, 10);

        PreparedStatement ps = connection.prepareStatement("SELECT max_score FROM quizes WHERE quiz_id = ?");
        ps.setInt(1, quizId);
        ResultSet result = ps.executeQuery();
        result.next();
        assertEquals(10, result.getInt("max_score"));
    }

    @Test
    public void testTakeQuiz() throws Exception {
        Account john = usersDao.getUser("john");
        int johnId = john.getId();

        Statement stmt = connection.createStatement();

        try {
            stmt.execute("ALTER TABLE quizes ADD COLUMN average_score DOUBLE DEFAULT 0");
        } catch (SQLException e) {
        }
        try {
            stmt.execute("ALTER TABLE quizes ADD COLUMN average_time DOUBLE DEFAULT 0");
        } catch (SQLException e) {
        }
        try {
            stmt.execute("ALTER TABLE quizes ADD COLUMN taken_by INT DEFAULT 0");
        } catch (SQLException e) {
        }

        stmt.execute("INSERT INTO quizes (quiz_name, quiz_description, user_id, max_score) VALUES ('Test Quiz', 'Description', " + johnId + ", 5)");

        ResultSet rs = stmt.executeQuery("SELECT quiz_id FROM quizes WHERE quiz_name = 'Test Quiz'");
        rs.next();
        int quizId = rs.getInt("quiz_id");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        usersDao.takeQuiz(johnId, quizId, 8, 120, timestamp, timestamp);

        assertEquals(1, usersDao.getTakenQuizesQuantity(johnId));

        PreparedStatement ps = connection.prepareStatement("SELECT score FROM taken_quizes WHERE user_id = ? AND quiz_id = ?");
        ps.setInt(1, johnId);
        ps.setInt(2, quizId);
        ResultSet result = ps.executeQuery();
        assertTrue(result.next());
        assertEquals(8, result.getInt("score"));
    }

    @Test
    public void testGetAnnouncements() throws Exception {
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO announcements (content) VALUES ('Test announcement 1')");
        stmt.execute("INSERT INTO announcements (content) VALUES ('Test announcement 2')");

        ArrayList<String> announcements = usersDao.getAnnouncements();
        assertEquals(2, announcements.size());
        assertTrue(announcements.contains("Test announcement 1"));
        assertTrue(announcements.contains("Test announcement 2"));

        stmt.execute("DROP TABLE announcements");
    }

    @Test
    public void testGetAchievements() throws Exception {
        Account john = usersDao.getUser("john");
        int johnId = john.getId();

        usersDao.addAchievement(johnId, "Test Achievement 1");
        usersDao.addAchievement(johnId, "Test Achievement 2");

        ArrayList<String> achievements = usersDao.getAchievements(johnId);
        assertEquals(2, achievements.size());
        assertTrue(achievements.contains("Test Achievement 1"));
        assertTrue(achievements.contains("Test Achievement 2"));

        ArrayList<String> noAchievements = usersDao.getAchievements(99999);
        assertEquals(0, noAchievements.size());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connection.close();
    }
}
