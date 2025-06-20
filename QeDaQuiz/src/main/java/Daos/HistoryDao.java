package Daos;

import quiz.history.History;
import quiz.questions.Question;
import quiz.quiz.Quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;

public class HistoryDao {

    Connection connection;
    private QuizDao quizDao;

    public HistoryDao(Connection con, QuizDao quizDao) throws SQLException {
        this.connection = con;
        this.quizDao = quizDao;
    }

    public History addHistory(int userId) {
        History history = new History(userId);
        String sql = "SELECT * FROM quizes WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int quizId = rs.getInt("quiz_id");

                    Quiz quiz = quizDao.getQuiz(quizId);

                    history.addQuiz(quiz);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving quiz history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

}
