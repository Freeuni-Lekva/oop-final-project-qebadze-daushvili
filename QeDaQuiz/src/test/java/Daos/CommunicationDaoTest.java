package Daos;

import org.junit.*;

import java.sql.*;

import static org.junit.Assert.*;

public class CommunicationDaoTest {
    private static Connection connection;
    private CommunicationDao dao;
    private int user1;
    private int user2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String url = "jdbc:mysql://localhost:3306/mgior23";
        connection = DriverManager.getConnection(url, "root", "");
        Statement stmt = connection.createStatement();

        stmt.execute("DROP TABLE IF EXISTS answers, questions, quizes, friend_requests, messages, users");

        stmt.execute("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(64)," +
                "hashed_password VARCHAR(64)," +
                "image_file VARCHAR(64))");

        stmt.execute("CREATE TABLE friend_requests (" +
                "request_id INT PRIMARY KEY AUTO_INCREMENT," +
                "from_user_id INT," +
                "to_user_id INT," +
                "status ENUM('PENDING', 'ACCEPTED') DEFAULT 'PENDING'," +
                "FOREIGN KEY (from_user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (to_user_id) REFERENCES users(user_id))");

        stmt.execute("CREATE TABLE messages (" +
                "message_id INT PRIMARY KEY AUTO_INCREMENT," +
                "from_user_id INT," +
                "to_user_id INT," +
                "type ENUM('FRIEND_REQUEST', 'CHALLENGE', 'NOTE')," +
                "content TEXT," +
                "quiz_id INT," +
                "FOREIGN KEY (from_user_id) REFERENCES users(user_id)," +
                "FOREIGN KEY (to_user_id) REFERENCES users(user_id))");
    }

    @Before
    public void setUp() throws Exception {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM messages");
        stmt.execute("DELETE FROM friend_requests");
        stmt.execute("DELETE FROM users");

        stmt.execute("INSERT INTO users (username, hashed_password, image_file) VALUES ('Alice', 'pass1', 'img1'), ('Bob', 'pass2', 'img2')", Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        user1 = rs.getInt(1);
        rs.next();
        user2 = rs.getInt(1);

        dao = new CommunicationDao(connection);
    }

    @Test
    public void testSendFriendRequest() throws Exception {
        dao.send_friend_request(user1, user2, "Hey let's be friends!");

        String status = dao.check_friends_status(user1, user2);
        assertEquals("PENDING", status);

        // Check message
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM messages WHERE from_user_id=" + user1);
        assertTrue(rs.next());
        assertEquals("FRIEND_REQUEST", rs.getString("type"));
        assertEquals("Hey let's be friends!", rs.getString("content"));
    }

    @Test
    public void testAcceptRequest() throws Exception {
        dao.send_friend_request(user1, user2, "Friend?");
        dao.send_friend_request(user2, user1, "Sure!");

        String status = dao.check_friends_status(user1, user2);
        assertEquals("ACCEPTED", status);
    }

    @Test
    public void testDeclineRequest() throws Exception {
        dao.send_friend_request(user1, user2, "Please?");
        dao.decline_friend_request(user2, user1);

        String status = dao.check_friends_status(user1, user2);
        assertEquals("NOT_SENT", status);
    }

    @Test
    public void testSendNoteMessage() throws Exception {
        dao.send_note_message(user1, user2, "Just a note");

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM messages WHERE from_user_id=" + user1);
        assertTrue(rs.next());
        assertEquals("NOTE", rs.getString("type"));
        assertEquals("Just a note", rs.getString("content"));
    }

    @Test
    public void testSendChallengeMessage() throws Exception {
        dao.send_challenge_message(user1, user2, "I challenge you!", 42);

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM messages WHERE from_user_id=" + user1);
        assertTrue(rs.next());
        assertEquals("CHALLENGE", rs.getString("type"));
        assertEquals("I challenge you!", rs.getString("content"));
        assertEquals(42, rs.getInt("quiz_id"));
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
