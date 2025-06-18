package testClasses;

import AccountManager.Account;
import AccountManager.PasswordHasher;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

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


}
