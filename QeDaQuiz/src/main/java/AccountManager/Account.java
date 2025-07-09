package AccountManager;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Account {
    private int id;
    private String password;
    private String username;
    private String photo;
    private int quizScore;
    boolean is_admin;
  
    public Account(String password, String username, String photo) throws NoSuchAlgorithmException {
        PasswordHasher hash = new PasswordHasher(password);
        this.password = hash.getHashedPassword();
        this.username = username;
        this.photo = photo;
        quizScore = 0;
    }

    public Account(String password, String username, String photo, boolean alreadyHashed) throws NoSuchAlgorithmException {
        if (alreadyHashed) {
            this.password = password;
            this.username = username;
            this.photo = photo;
            quizScore = 0;
        }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setquizScore(int quizScore) {
        this.quizScore = quizScore;
    }
    public int getQuizScore(){
        return quizScore;
    }
    public void makeAdmin(){
        is_admin=true;
    }
    public boolean isAdmin(){
        return is_admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
