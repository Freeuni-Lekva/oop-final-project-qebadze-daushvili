package Daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDao {
    Connection con;
    public AdminDao(Connection con){
        this.con = con;
    }
    public void makeAnnouncement(int admin_user_id, String content) throws SQLException {
        String query="INSERT INTO announcements (admin_user_id, content) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, admin_user_id);
        ps.setString(2, content);
        ps.executeUpdate();
    }

    public void removeUser(int user_id) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, user_id);
        ps.executeUpdate();
    }

    public void removeQuiz(int quiz_id) throws SQLException {
        String query = "DELETE FROM quizes WHERE quiz_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, quiz_id);
        ps.executeUpdate();
    }

    public void removeQuizHistory(int quiz_id) throws SQLException {
        String query="DELETE FROM taken_quizes WHERE quiz_id=?";
        PreparedStatement ps=con.prepareStatement(query);
        ps.setInt(1, quiz_id);
        ps.executeUpdate();
    }

    public void makeAdmin(int user_id) throws SQLException {
        String query="UPDATE users SET is_admin=1 WHERE user_id=?";
        PreparedStatement ps=con.prepareStatement(query);
        ps.setInt(1, user_id);
        ps.executeUpdate();
    }

    public int getUserNumber() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1); // first column of the result
        }
        return count;
    }

    public int getTakenQuizNumber() throws SQLException {
        String sql = "SELECT COUNT(*) FROM taken_quizes";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1); // first column of the result
        }
        return count;
    }

}
