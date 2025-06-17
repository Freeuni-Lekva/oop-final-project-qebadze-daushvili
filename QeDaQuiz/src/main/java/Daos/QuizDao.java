package Daos;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;

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
