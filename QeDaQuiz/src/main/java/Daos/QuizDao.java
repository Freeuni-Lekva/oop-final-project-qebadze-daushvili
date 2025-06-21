package Daos;
import Constantas.Constantas;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;
import quiz.quiz.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDao {

    Connection con;

    public QuizDao(Connection conn) throws SQLException {
        this.con = conn;
    }

    public List<String> findAllCorrectAnswers(int questionId) throws SQLException {
        List<String> correctAnswers = new ArrayList<>();
        String st="SELECT * FROM answers WHERE question_id=? AND is_correct=1";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, questionId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            correctAnswers.add(rs.getString("answer"));
        }
        return correctAnswers;
    }

    public List<String> findAllWrongAnswers(int questionId) throws SQLException {
        List<String> wrongAnswers = new ArrayList<>();
        String st="SELECT * FROM answers WHERE question_id=? AND is_correct=0";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, questionId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            wrongAnswers.add(rs.getString("answer"));
        }
        return wrongAnswers;
    }

    public void addQuestion(Question question, int quizId) throws SQLException {
        String st = "INSERT INTO questions (quiz_id, type, prompt) VALUES (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(st, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, quizId);
        ps.setString(2, question.getType());
        ps.setString(3, question.getPrompt());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int questionId = rs.getInt(1);
            question.setQuestionId(questionId);
        }
    }

    public void addAchievement(int user_id, String achievement) throws SQLException {
        //check if user already has this achievement
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

        //if not, adds it to the achievements table
        String st = "INSERT INTO achievements (user_id, achievement) VALUES (?, ?);";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, user_id);
        ps.setString(2, achievement);
        ps.executeUpdate();
    }

    public void addQuiz(Quiz quiz) throws SQLException {
        //add to quizes table
        String st = "INSERT INTO quizes (quiz_name, quiz_description, user_id) VALUES (?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(st, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, quiz.getQuizName());
        ps.setString(2, quiz.getQuizDescription());
        ps.setInt(3, quiz.getUserId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int quiz_id = rs.getInt(1);
            quiz.setQuizId(quiz_id);
        }

        List<Question> questions = quiz.getQuestions();
        for(Question question : questions){
            addQuestion(question,quiz.getQuizId());
        }

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

    public void removeQuestion(int questionId) throws SQLException {
        String st = "DELETE FROM questions WHERE question_id=?";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, questionId);
        ps.executeUpdate();
    }

    public void removeQuiz(int quizId) throws SQLException {
        Quiz quiz = getQuiz(quizId);
        if (quiz == null) {
            return;
        }
        List<Question> questions = quiz.getQuestions();
        for(Question question : questions){
            removeQuestion(question.getQuestionId());
        }

        String st = "DELETE FROM quizes WHERE quiz_id=?";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, quizId);
        ps.executeUpdate();
    }

    public Quiz getQuiz(int quizId) throws SQLException {
        Quiz quiz = null;
        String st="SELECT * FROM quizes WHERE quiz_id=?";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, quizId);
        ResultSet rs = ps.executeQuery();
        if(!rs.next()) {
            return null;
        }
        int quiz_id = rs.getInt("quiz_id");
        String quiz_name = rs.getString("quiz_name");
        String quiz_description = rs.getString("quiz_description");
        int user_id = rs.getInt("user_id");
        List<Question> questions = getQuizQuestions(quiz_id);
        quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, questions);
        return quiz;
    }

    public List<Question> getQuizQuestions(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<Question>();
        String st="SELECT * FROM questions WHERE quiz_id=?";
        PreparedStatement pst=con.prepareStatement(st);
        pst.setInt(1, quizId);
        ResultSet rs=pst.executeQuery();
        while(rs.next()){
            String prompt=rs.getString("prompt");
            String type=rs.getString("type");
            int question_id=rs.getInt("question_id");
            List<String> correctAnswers = findAllCorrectAnswers(question_id);
            if(type.equals("RESPONSE_QUESTION")||type.equals("FILL_BLANK")||type.equals("PICTURE_RESPONSE")){
                Question q=new ResponseQuestion(prompt, correctAnswers, type);
                questions.add(q);
            }
            if(type.equals("MULTIPLE_CHOICE")){
                List<String> wrongAnswers = findAllWrongAnswers(question_id);
                Question q=new MultipleChoiceQuestion(prompt, correctAnswers, wrongAnswers, type);
                questions.add(q);
            }
        }
        return questions;
    }

}
