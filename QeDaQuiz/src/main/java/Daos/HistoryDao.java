package Daos;

import AccountManager.Account;
import quiz.history.History;
import quiz.history.Stat;
import quiz.questions.Question;
import quiz.quiz.Quiz;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDao {

    Connection connection;
    private QuizDao quizDao;
    private UsersDao usersDao;
    public HistoryDao(Connection con, QuizDao quizDao, UsersDao usersDao) throws SQLException {
        this.connection = con;
        this.quizDao = quizDao;
        this.usersDao = usersDao;
    }

    public History getUserHistory(int userId) {
        History history = new History(userId);
        String sql = "SELECT * FROM taken_quizes WHERE user_id = ? ORDER BY taken_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int quizId = rs.getInt("quiz_id");
                    int gottenScore=rs.getInt("score");
                    Quiz quiz = quizDao.getQuiz(quizId);
                    quiz.setGottenScore(gottenScore);
                    history.addQuiz(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    //needs testing
    public History getUserCreatingHistory(int userId) {
        History history = new History(userId);
        String sql = "SELECT * FROM quizes WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int quiz_id = rs.getInt("quiz_id");
                    String quiz_name = rs.getString("quiz_name");
                    String quiz_description = rs.getString("quiz_description");
                    int user_id = rs.getInt("user_id");
                    List<Question> questions = quizDao.getQuizQuestions(quiz_id);
                    Quiz quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, questions);
                    history.addQuiz(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public ArrayList<Account> getQuizHistory(int quizId) {
        ArrayList<Account> history = new ArrayList<>();
        String sql = "SELECT * FROM taken_quizes WHERE quiz_id = ? ORDER BY taken_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quizId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    int gottenScore=rs.getInt("score");
                    Account acc=usersDao.getUser(userId);
                    acc.setquizScore(gottenScore);
                    history.add(acc);
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    //gasatestia
    public List<Stat> getQuizStats(int quizId, boolean last){
        List<Stat> stats = new ArrayList<>();
        String sql = "SELECT * FROM taken_quizes WHERE quiz_id = ? ";
        if(last){
            sql = "SELECT * FROM taken_quizes WHERE quiz_id = ? AND taken_at > NOW()- INTERVAL 1 DAY ORDER BY taken_at DESC";
        }
        String sql2 = "SELECT * FROM quizes WHERE quiz_id = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
            stmt.setInt(1, quizId);
            stmt2.setInt(1, quizId);
            ResultSet quizRs = stmt2.executeQuery();
            int maxScore = -1;
            float avgScore = 0;
            float avgTime = 0;
            int attempts = 0;
            if(quizRs.next()) {
                maxScore = quizRs.getInt("max_score");
                avgScore = quizRs.getFloat("average_score");
                avgTime = quizRs.getFloat("average_time");
                attempts = quizRs.getInt("taken_by");
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    int gottenScore=rs.getInt("score");
                    Timestamp takenAt=rs.getTimestamp("taken_at");
                    Timestamp finishedAt=rs.getTimestamp("finished_at");
                    long millis = finishedAt.getTime() - takenAt.getTime();
                    millis = millis/1000;
                    Stat stat = new Stat(userId, quizId, gottenScore, maxScore, millis, finishedAt, avgScore,  avgTime, attempts);
                    stats.add(stat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    //gasatestia
    public List<Stat> getQuizStatsByUser(int quizId, int userId) {
        List<Stat> stats = new ArrayList<>();
        String sql = "SELECT * FROM taken_quizes WHERE quiz_id = ? AND user_id = ?";
        String sql2 = "SELECT * FROM quizes WHERE quiz_id = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
            stmt.setInt(1, quizId);
            stmt.setInt(2, userId);
            stmt2.setInt(1, quizId);
            ResultSet quizRs = stmt2.executeQuery();
            int maxScore = -1;
            float avgScore = 0;
            float avgTime = 0;
            int attempts = 0;
            if(quizRs.next()) {
                maxScore = quizRs.getInt("max_score");
                avgScore = quizRs.getFloat("average_score");
                avgTime = quizRs.getFloat("average_time");
                attempts=quizRs.getInt("taken_by");
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int usersId = rs.getInt("user_id");
                    int gottenScore=rs.getInt("score");
                    Timestamp takenAt=rs.getTimestamp("taken_at");
                    Timestamp finishedAt=rs.getTimestamp("finished_at");
                    long millis = finishedAt.getTime() - takenAt.getTime();
                    millis = millis/1000;
                    Stat stat = new Stat(usersId, quizId, gottenScore, maxScore, millis, finishedAt, avgScore,  avgTime, attempts);
                    stats.add(stat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }



}
