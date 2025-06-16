package Daos;

import AccountManager.Account;
import AccountManager.PasswordHasher;

import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UsersDao {
    Connection con;


    public UsersDao(Connection conn) throws SQLException {
        this.con = conn;
    }

    public boolean checkAccountPassword(String username, String password) throws SQLException, NoSuchAlgorithmException {
        if(!checkAccountName(username)) return false;
        PasswordHasher hash = new PasswordHasher(password);
        String sql = "SELECT * FROM users WHERE username = ? AND hashedpassword = ?";

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
        String sql = "INSERT INTO users (username, hashedpassword, imagefile) " + "VALUES (?, ?, ?);";
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


}
