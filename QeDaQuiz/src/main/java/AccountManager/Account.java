package AccountManager;

import java.security.NoSuchAlgorithmException;

public class Account {
    private int id;
    private String password;
    private String username;
    private String photo;

    public Account(String password, String username, String photo) throws NoSuchAlgorithmException {
        PasswordHasher hash = new PasswordHasher(password);
        this.password = hash.getHashedPassword();
        this.username = username;
        this.photo = photo;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getPhoto() {
        return photo;
    }

    public void setId(int id) {
        this.id = id;
    }
}
