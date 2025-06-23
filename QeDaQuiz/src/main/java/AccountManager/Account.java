package AccountManager;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Account {
    private int id;
    private String password;
    private String username;
    private String photo;
    private int quizesMade;
    private int quizesTaken;
    private int quizScore;
    boolean is_admin;
    public Account(String password, String username, String photo) throws NoSuchAlgorithmException {
        PasswordHasher hash = new PasswordHasher(password);
        this.password = hash.getHashedPassword();
        this.username = username;
        this.photo = photo;
        quizesMade = 0;
        quizesTaken = 0;
        quizScore = 0;
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

    public int getQuizesMade() {
        return quizesMade;
    }

    public int getQuizesTaken() {
        return quizesTaken;
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
}
