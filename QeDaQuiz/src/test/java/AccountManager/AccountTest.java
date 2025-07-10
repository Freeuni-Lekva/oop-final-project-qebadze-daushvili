package AccountManager;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void testAccount1() throws NoSuchAlgorithmException {
        Account account = new Account("1234", "Frankenstein", "url//photo");
        PasswordHasher hasher = new PasswordHasher("1234");
        assertEquals(account.getPassword(), hasher.getHashedPassword());
        assertEquals(account.getUsername(), "Frankenstein");
        assertEquals(account.getPhoto(), "url//photo");
    }

    @Test
    public void testAccount2() throws NoSuchAlgorithmException {
        Account account = new Account("aabbcc", "Elton", "url//photo123");
        PasswordHasher hasher = new PasswordHasher("aabbcc");
        account.setId(7);
        assertEquals(account.getPassword(), hasher.getHashedPassword());
        assertEquals(account.getUsername(), "Elton");
        assertEquals(account.getPhoto(), "url//photo123");
        assertEquals(account.getId(), 7);
    }

    @Test
    public void testAccount3() throws NoSuchAlgorithmException {
        Account account = new Account("1234", "Frankenstein", "url//photo");
        account.setquizScore(3);
        assertEquals(3, account.getQuizScore());
        assertFalse(account.isAdmin());
        account.makeAdmin();
        assertTrue(account.isAdmin());
    }

    @Test
    public void testEquals() throws NoSuchAlgorithmException {
        Account account1 = new Account("pass", "user", "photo");
        Account account2 = new Account("pass", "user", "photo");
        Account account3 = new Account("different", "user", "photo");

        account1.setId(1);
        account2.setId(1);
        account3.setId(2);

        assertTrue(account1.equals(account1));
        assertTrue(account1.equals(account2));
        assertFalse(account1.equals(account3));
        assertFalse(account1.equals(null));
        assertFalse(account1.equals("not an account"));
    }

    @Test
    public void testAlreadyHashedConstructor() throws NoSuchAlgorithmException {
        Account account = new Account("hashedPassword123", "TestUser", "photo.jpg", true);
        assertEquals("hashedPassword123", account.getPassword());
        assertEquals("TestUser", account.getUsername());
        assertEquals("photo.jpg", account.getPhoto());
        assertEquals(0, account.getQuizScore());
        assertFalse(account.isAdmin());
    }


}
