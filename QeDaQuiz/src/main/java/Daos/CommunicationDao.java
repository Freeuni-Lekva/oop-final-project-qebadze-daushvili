package Daos;

import AccountManager.Account;
import AccountManager.Message;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommunicationDao {
    Connection con;
    UsersDao usersDao;
    public CommunicationDao(Connection conn, UsersDao usersDao) throws SQLException {
        this.con = conn;
        this.usersDao=usersDao;
    }
    public String check_friends_status(int first_user_id, int second_user_id) throws SQLException {
        String stmt="SELECT status FROM friend_requests WHERE (from_user_id=? AND to_user_id=?) OR (from_user_id=? AND to_user_id=?) " ;
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, first_user_id);
        ps.setInt(2, second_user_id);
        ps.setInt(3, second_user_id);
        ps.setInt(4, first_user_id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            String status=rs.getString("status");
            return status;
        }
        return "NOT_SENT";
    }

    public void accept_request(int first_user_id, int second_user_id) throws SQLException {
        String stmt = "UPDATE friend_requests SET status = 'ACCEPTED' " +
                     "WHERE ((from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?)) " +
                        "AND status = 'PENDING'";
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, first_user_id);
        ps.setInt(2, second_user_id);
        ps.setInt(3, second_user_id);
        ps.setInt(4, first_user_id);
        ps.executeUpdate();
    }



    public void send_friend_request(int sender_id, int receiver_id, String message) throws SQLException {
        String cur_status=check_friends_status(sender_id,receiver_id);
        if(cur_status.equals("NOT_SENT")) {
            String stmt = "INSERT INTO friend_requests (from_user_id, to_user_id) VALUES (?,?)";
            PreparedStatement st = con.prepareStatement(stmt);
            st.setInt(1, sender_id);
            st.setInt(2, receiver_id);
            st.executeUpdate();
            send_request_message(sender_id, receiver_id, message);
        }
        if(cur_status.equals("PENDING")){
            String stmt="SELECT * FROM friend_requests WHERE from_user_id=? AND to_user_id=?";
            PreparedStatement st = con.prepareStatement(stmt);
            st.setInt(1, receiver_id);
            st.setInt(2, sender_id);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                accept_request(sender_id,receiver_id);
            }
        }
    }

    public void decline_friend_request(int sender_id, int receiver_id) throws SQLException {
        if(check_friends_status(sender_id,receiver_id).equals("PENDING")) {
            delete_friend_request(sender_id,receiver_id);
        }
    }

    public void delete_friend_request(int first_user_id, int second_user_id) throws SQLException {
        String stmt = "DELETE FROM friend_requests " +
                "WHERE (from_user_id = ? AND to_user_id = ?) " +
                "   OR (from_user_id = ? AND to_user_id = ?)";
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, first_user_id);
        ps.setInt(2, second_user_id);
        ps.setInt(3, second_user_id);
        ps.setInt(4, first_user_id);
        ps.executeUpdate();
    }

    public void send_note_message(int sender_id, int receiver_id, String message) throws SQLException {
        String stmt="INSERT INTO messages (from_user_id, to_user_id, type, content) VALUES (?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, sender_id);
        ps.setInt(2, receiver_id);
        ps.setString(3, "NOTE");
        ps.setString(4, message);
        ps.executeUpdate();
    }

    public void send_request_message(int sender_id, int receiver_id, String message) throws SQLException {
        String stmt="INSERT INTO messages (from_user_id, to_user_id, type, content) VALUES (?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, sender_id);
        ps.setInt(2, receiver_id);
        ps.setString(3, "FRIEND_REQUEST");
        ps.setString(4, message);
        ps.executeUpdate();
    }

    public void send_challenge_message(int sender_id, int receiver_id, String message, int quiz_id) throws SQLException {
        String stmt="INSERT INTO messages (from_user_id, to_user_id, type, content, quiz_id) VALUES (?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(stmt);
        ps.setInt(1, sender_id);
        ps.setInt(2, receiver_id);
        ps.setString(3, "CHALLENGE");
        ps.setString(4, message);
        ps.setInt(5, quiz_id);
        ps.executeUpdate();
    }

    public ArrayList<Message> getAllSentMessages(int from_user_id) throws SQLException, NoSuchAlgorithmException {
        ArrayList<Message> messages=new ArrayList<>();
        String sql="SELECT * FROM messages WHERE from_user_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, from_user_id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int to_user_id=rs.getInt("to_user_id");
            String type=rs.getString("type");
            String content=rs.getString("content");
            int quizId=rs.getInt("quiz_id");
            Account sender=usersDao.getUser(from_user_id);
            Account receiver=usersDao.getUser(to_user_id);
            Message message=new Message(content, type, sender, receiver, quizId);
            messages.add(message);
        }
        return messages;
    }

    public ArrayList<Message> getAllGottenMessages(int to_user_id) throws SQLException, NoSuchAlgorithmException {
        ArrayList<Message> messages=new ArrayList<>();
        String sql="SELECT * FROM messages WHERE to_user_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, to_user_id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int from_user_id=rs.getInt("from_user_id");
            String type=rs.getString("type");
            String content=rs.getString("content");
            int quizId=rs.getInt("quiz_id");
            Account sender=usersDao.getUser(from_user_id);
            Account receiver=usersDao.getUser(to_user_id);
            Message message=new Message(content, type, sender, receiver, quizId);

            messages.add(message);
        }
        return messages;
    }

    public ArrayList<Account> getAllRequests(int user_id) throws SQLException, NoSuchAlgorithmException {
        ArrayList<Account> accounts=new ArrayList<>();
        String sql="SELECT * FROM friend_requests WHERE to_user_id = ? AND status='PENDING'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, user_id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int from_id=rs.getInt("from_user_id");
            Account usr=usersDao.getUser(from_id);
            accounts.add(usr);
        }
        return accounts;
    }

    public ArrayList<Account> getAllFriends(int user_id) throws SQLException, NoSuchAlgorithmException {
        ArrayList<Account> accounts=new ArrayList<>();
        String sql="SELECT * FROM friend_requests " +
                    "WHERE (from_user_id = ? OR to_user_id = ?) AND status='ACCEPTED'";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, user_id);
        ps.setInt(2, user_id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int from_id=rs.getInt("from_user_id");
            int to_id=rs.getInt("to_user_id");
            if(from_id==user_id){
                from_id=to_id;
            }
            Account usr=usersDao.getUser(from_id);
            accounts.add(usr);
        }
        return accounts;
    }




}
