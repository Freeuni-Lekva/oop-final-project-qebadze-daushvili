package Daos;

import AccountManager.Account;
import quiz.history.History;
import quiz.questions.Question;
import quiz.quiz.Quiz;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Deque;
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
        String sql = "SELECT * FROM quizes WHERE user_id = ? ORDER BY taken_at DESC";

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

}
