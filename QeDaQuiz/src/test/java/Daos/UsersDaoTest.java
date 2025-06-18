package Daos;
import Daos.UsersDao;
import AccountManager.Account;
import org.junit.*;

import java.sql.*;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class UsersDaoTest {

    private static Connection connection;
    private UsersDao usersDao;

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

        stmt.execute("DROP TABLE IF EXISTS users");
        stmt.execute("CREATE TABLE users (" +
                "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(64)," +
                "hashed_password VARCHAR(64)," +
                "image_file VARCHAR(64))");
    }

    @Before
    public void setUp() throws Exception {
        Statement stmt = connection.createStatement();
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

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        connection.close();
    }
}
