package AccountManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    private String hashedPassword;

    private String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();

        for (int i=0; i<bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val<16) buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }

    public PasswordHasher(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA");
        byte[] res = md.digest(password.getBytes());
        hashedPassword = hexToString(res);
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
