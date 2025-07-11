package Daos;

import AccountManager.Account;
import AccountManager.PasswordHasher;
import Constantas.Constantas;
import quiz.quiz.Quiz;


import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class UsersDao {
    Connection con;


    public UsersDao(Connection conn) throws SQLException {
        this.con = conn;
    }

    public Account getUser(int id) throws SQLException, NoSuchAlgorithmException {
        String query="SELECT * FROM users WHERE user_id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("hashed_password");
            String image = rs.getString("image_file");
            Account acc=new Account(password, username, image, true);
            acc.setId(rs.getInt("user_id"));
            return acc;
        }
        else{
            return null;
        }

    }

    public Account getUser(String username) throws SQLException, NoSuchAlgorithmException {
        String query="SELECT * FROM users WHERE username=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            String password = rs.getString("hashed_password");
            String image = rs.getString("image_file");
            int id=rs.getInt("user_id");
            Account acc=new Account(password, username, image, true);
            acc.setId(id);
            return acc;
        }
        else{
            return null;
        }
    }

    public ArrayList<Account> searchUsersByUsername(String searchTerm, int excludeUserId) throws SQLException, NoSuchAlgorithmException {
        ArrayList<Account> users = new ArrayList<>();
        String query = "SELECT user_id, username, hashed_password, image_file FROM users WHERE username LIKE ? AND user_id != ? ORDER BY username LIMIT 20";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setInt(2, excludeUserId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("hashed_password");
                String image = rs.getString("image_file");
                int id = rs.getInt("user_id");

                Account user = new Account(password, username, image, true);
                user.setId(id);
                users.add(user);
            }
        }

        return users;
    }

    public boolean checkAccountPassword(String username, String password) throws SQLException, NoSuchAlgorithmException {
        if(!checkAccountName(username)) return false;
        PasswordHasher hash = new PasswordHasher(password);
        String sql = "SELECT * FROM users WHERE username = ? AND hashed_password = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hash.getHashedPassword());

            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next();
            }
        }
    }

    public void addAccount(Account account) throws SQLException {
        if(checkAccountName(account.getUsername())){
            return;
        }
        String sql = "INSERT INTO users (username, hashed_password, image_file) VALUES (?, ?, ?);";
        PreparedStatement preparedStatement = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, account.getPassword());
        preparedStatement.setString(3, account.getPhoto());

        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            account.setId(id);
        }
    }

    public boolean checkAccountName(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next();
            }
        }
    }

    public void addAchievement(int user_id, String achievement) throws SQLException {
        String sql = "SELECT * FROM achievements WHERE user_id = ? AND achievement = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, achievement);
            try (ResultSet set = preparedStatement.executeQuery()) {
                if (set.next()) {
                    return;
                }
            }
        }
        String st = "INSERT INTO achievements (user_id, achievement) VALUES (?, ?);";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, user_id);
        ps.setString(2, achievement);
        ps.executeUpdate();
    }

    public int getTakenQuizesQuantity (int user_id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            try (ResultSet set = preparedStatement.executeQuery()) {
                if (set.next()) {
                    return set.getInt("quizes_taken");
                }
            }
        }
        return 0;
    }

    public int getMadeQuizesQuantity (int user_id) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            try (ResultSet set = preparedStatement.executeQuery()) {
                if (set.next()) {
                    return set.getInt("quizes_made");
                }
            }
        }
        return 0;
    }

    public boolean hasAchievement (int user_id, String achievement) throws SQLException {
        String sql = "SELECT * FROM achievements WHERE user_id = ? AND achievement = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, achievement);
            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next();
            }
        }
    }

    public void updateQuizMaxScore(int quiz_id, int newMaxScore) throws SQLException {
        String str = "UPDATE quizes SET max_score = ? WHERE quiz_id = ?";
        PreparedStatement pr = con.prepareStatement(str, PreparedStatement.RETURN_GENERATED_KEYS);
        pr.setInt(1, newMaxScore);
        pr.setInt(2, quiz_id);
        pr.executeUpdate();
    }

    public void updateTakenQuiz(int user_id, int quiz_id, int score) throws SQLException {
        //update quantity
        String st = "SELECT * FROM users WHERE user_id = ?";
        int quizes = 0;
        try (PreparedStatement prSt = con.prepareStatement(st)) {
            prSt.setInt(1, user_id);
            try (ResultSet s = prSt.executeQuery()) {
                if (s.next()) {
                    quizes = s.getInt("quizes_taken");
                }
            }
        }
        quizes++;
        String sqlSt = "UPDATE users SET quizes_taken = ? WHERE user_id = ?";
        try (PreparedStatement prSt = con.prepareStatement(sqlSt)) {
            prSt.setInt(1, quizes);
            prSt.setInt(2, user_id);
            prSt.executeUpdate();
        }

        //update achievements
        if (quizes == Constantas.QUIZ_MACHINE_QUIZES_TAKEN) {
            addAchievement(user_id,"Quiz Machine");
        }

        checkMaxScore(quiz_id, score, user_id);
    }

    //if score > maxScore updates quizes & achievements tables
    public void checkMaxScore(int quiz_id, int score, int user_id) throws SQLException {
        String sql = "SELECT * FROM quizes WHERE quiz_id = ?";
        try (PreparedStatement prSt = con.prepareStatement(sql)) {
            prSt.setInt(1, quiz_id);
            try (ResultSet set = prSt.executeQuery()) {
                if (set.next()) {
                    int maxScore = set.getInt("max_score");
                    if (score > maxScore) {
                        updateQuizMaxScore(quiz_id, score);
                        addAchievement(user_id,"I am the Greatest");
                    }
                }
            }
        }
    }

    public void takeQuiz(int user_id, int quiz_id, int score, long time, Timestamp start, Timestamp end) throws SQLException {
        String addSql = "INSERT INTO taken_quizes (quiz_id, user_id, score, taken_at, finished_at) VALUES (?, ?, ?, ?, ?)";
        String changeSql="UPDATE quizes SET average_score = (average_score * taken_by + ?)/(taken_by + 1), average_time = (average_time * taken_by + ?)/(taken_by + 1), taken_by = taken_by + 1 WHERE quiz_id = ?";
        try (PreparedStatement ps = con.prepareStatement(addSql)) {
            ps.setInt(1, quiz_id);
            ps.setInt(2, user_id);
            ps.setInt(3, score);
            ps.setTimestamp(4, start);
            ps.setTimestamp(5, end);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement(changeSql)) {
            ps.setInt(1, score);
            ps.setLong(2, time);
            ps.setInt(3, quiz_id);
            ps.executeUpdate();
        }
        updateTakenQuiz(user_id, quiz_id, score);
    }

    public void makeQuiz(Quiz quiz) throws SQLException {
        //add to quizes made by user
        String sql = "SELECT quizes_made FROM users WHERE user_id = ?";
        int quizes = 0;
        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, quiz.getUserId());
            try (ResultSet set = preparedStatement.executeQuery()) {
                if (set.next()) {
                    quizes = set.getInt("quizes_made");
                }
            }
        }

        quizes++;
        String sqlSt = "UPDATE users SET quizes_made = ? WHERE user_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(sqlSt)) {
            preparedStatement.setInt(1, quizes);
            preparedStatement.setInt(2, quiz.getUserId());
            preparedStatement.executeUpdate();
        }

        //add to achievements
        if (quizes== Constantas.AMATEUR_AUTHOR_QUIZES_MADE) {
            addAchievement(quiz.getUserId(),"AMATEUR AUTHOR");
        }

        if (quizes==Constantas.PROLIFIC_AUTHOR_QUIZES_MADE) {
            addAchievement(quiz.getUserId(),"PROLIFIC AUTHOR");
        }

        if (quizes==Constantas.PRODIGIOUS_AUTHOR_QUIZES_MADE) {
            addAchievement(quiz.getUserId(),"PRODIGIOUS AUTHOR");
        }
    }

    public void takeQuizInPracticeMode(int user_id, int quiz_id) throws SQLException {
        addAchievement(user_id,"Practice Makes Perfect");
    }

    public ArrayList<String> getAnnouncements() throws SQLException {
        ArrayList<String> ans=new ArrayList<>();
        String sql="SELECT * FROM announcements ORDER BY made_at DESC";
        PreparedStatement stmt=con.prepareStatement(sql);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()) {
            String announcement = rs.getString("content");
            ans.add(announcement);
        }
        return ans;
    }

    public ArrayList<String> getAchievements(int userId) throws SQLException {
        ArrayList<String> ans=new ArrayList<>();
        String sql="SELECT * FROM achievements WHERE user_id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()) {
            String achievement = rs.getString("achievement");
            ans.add(achievement);
        }
        return ans;
    }

    public void zeroNumMessages(int userId) throws SQLException {
        String sql="UPDATE users SET num_messages = 0 WHERE user_id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,userId);
        stmt.executeUpdate();
    }

    public void increaseNumMessages(int userId) throws SQLException {
        String sql="UPDATE users SET num_messages = num_messages+1 WHERE user_id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,userId);
        stmt.executeUpdate();
    }

    public Integer getNumMessages(int userId) throws SQLException {
        String sql="SELECT num_messages FROM users WHERE user_id=?";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setInt(1,userId);
        ResultSet rs=stmt.executeQuery();
        rs.next();
        return rs.getInt("num_messages");
    }

}
