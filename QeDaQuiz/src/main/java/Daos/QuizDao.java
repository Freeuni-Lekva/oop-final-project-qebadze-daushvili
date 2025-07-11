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

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            List<String> wrongAnswers = new ArrayList<>();
            wrongAnswers.addAll(mcq.getWrongAnswers());
            for (int i=0; i<wrongAnswers.size(); i++) {
                String sqlst = "INSERT INTO answers (question_id, answer, is_correct) VALUES (?, ?, ?);";
                PreparedStatement pst = con.prepareStatement(sqlst);
                pst.setInt(1,question.getQuestionId());
                pst.setString(2,wrongAnswers.get(i));
                pst.setBoolean(3,false);
                pst.executeUpdate();
            }
            String correctAnswer = question.getCorrectAnswers().get(0);
            String sqlst = "INSERT INTO answers (question_id, answer, is_correct) VALUES (?, ?, ?);";
            PreparedStatement pst = con.prepareStatement(sqlst);
            pst.setInt(1,question.getQuestionId());
            pst.setString(2,correctAnswer);
            pst.setBoolean(3,true);
            pst.executeUpdate();
        } else {
            List<String> correctAnswers = new ArrayList<>();
            correctAnswers.addAll(question.getCorrectAnswers());
            for (int i=0; i<correctAnswers.size(); i++) {
                String sqlst = "INSERT INTO answers (question_id, answer, is_correct) VALUES (?, ?, ?);";
                PreparedStatement pst = con.prepareStatement(sqlst);
                pst.setInt(1,question.getQuestionId());
                pst.setString(2,correctAnswers.get(i));
                pst.setBoolean(3,true);
                pst.executeUpdate();
            }
        }
    }

    public void addQuiz(Quiz quiz) throws SQLException {
        //add to quizes table
        String st = "INSERT INTO quizes (quiz_name, quiz_description, user_id, isRandom) VALUES (?, ?, ?, ?);";
        PreparedStatement ps = con.prepareStatement(st, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, quiz.getQuizName());
        ps.setString(2, quiz.getQuizDescription());
        ps.setInt(3, quiz.getUserId());
        ps.setBoolean(4, quiz.isRandom());
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

        String updateQuery = "UPDATE users SET quizes_made = quizes_made + 1 WHERE user_id = ?;";
        PreparedStatement updatePs = con.prepareStatement(updateQuery);
        updatePs.setInt(1, quiz.getUserId());
        updatePs.executeUpdate();
    }

    public void removeQuestion(int questionId) throws SQLException {
        String st = "DELETE FROM questions WHERE question_id=?";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setInt(1, questionId);
        ps.executeUpdate();
        String st2 = "DELETE FROM answers WHERE question_id=?";
        PreparedStatement pst2 = con.prepareStatement(st2);
        pst2.setInt(1, questionId);
        pst2.executeUpdate();
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
        boolean isRandom = rs.getBoolean("isRandom");
        quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, questions,isRandom);
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
            if(type.equals(Constantas.QUESTION_RESPONSE)||type.equals(Constantas.FILL_IN_THE_BLANK)||type.equals(Constantas.PICTURE_RESPONSE)){
                Question q=new ResponseQuestion(prompt, correctAnswers, type);
                questions.add(q);
            }
            if(type.equals(Constantas.MULTIPLE_CHOICE)){
                List<String> wrongAnswers = findAllWrongAnswers(question_id);
                Question q=new MultipleChoiceQuestion(prompt, correctAnswers, wrongAnswers, type);
                questions.add(q);
            }
        }
        return questions;
    }

    //needs testing
    public ArrayList<Quiz> getQuizes() throws SQLException {
        ArrayList<Quiz> ans=new ArrayList<>();
        String st="SELECT * FROM quizes ORDER BY created_at DESC";
        PreparedStatement ps = con.prepareStatement(st);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int quiz_id = rs.getInt("quiz_id");
            String quiz_name = rs.getString("quiz_name");
            String quiz_description = rs.getString("quiz_description");
            int user_id = rs.getInt("user_id");
            boolean isRandom = rs.getBoolean("isRandom");
            List<Question> questions = getQuizQuestions(quiz_id);
            Quiz quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, questions, isRandom);
            ans.add(quiz);
        }
        return ans;
    }


    //need testing
    public int numberOfQuestions() throws SQLException {
        return getQuizes().size();
    }

    //needs tests
    public ArrayList<Quiz> getQuizzesByUserId(int userId) throws SQLException {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT * FROM quizes WHERE user_id = ? ORDER BY quiz_id DESC LIMIT 20";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                String quizName = rs.getString("quiz_name");
                String description = rs.getString("description");
                boolean isRandom = rs.getBoolean("isRandom");
                List<Question> questions =(List<Question>) rs.getObject("questions", List.class);
                Quiz quiz = new Quiz(quizId, quizName, description, userId, questions, isRandom);

                quizzes.add(quiz);
            }
        }

        return quizzes;
    }

    //needs testing
    public ArrayList<Quiz> getPopularQuizes() throws SQLException {
        ArrayList<Quiz> ans=new ArrayList<>();
        String st="SELECT * FROM quizes ORDER BY taken_by DESC";
        PreparedStatement ps = con.prepareStatement(st);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int quiz_id = rs.getInt("quiz_id");
            String quiz_name = rs.getString("quiz_name");
            String quiz_description = rs.getString("quiz_description");
            int user_id = rs.getInt("user_id");
            boolean isRandom = rs.getBoolean("isRandom");
            List<Question> questions = getQuizQuestions(quiz_id);
            Quiz quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, questions, isRandom);
            ans.add(quiz);
        }
        return ans;
    }

    public ArrayList<Quiz> getAllQuizNamesAndIds() throws SQLException {
        ArrayList<Quiz> quizzes = new ArrayList<>();
        String st="SELECT quiz_id, quiz_name, quiz_description, user_id FROM quizes ORDER BY quiz_name ASC";
        PreparedStatement ps = con.prepareStatement(st);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int quiz_id = rs.getInt("quiz_id");
            String quiz_name = rs.getString("quiz_name");
            String quiz_description = rs.getString("quiz_description");
            int user_id = rs.getInt("user_id");
            boolean isRandom = rs.getBoolean("isRandom");
            Quiz quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, new ArrayList<>(), isRandom);
            quizzes.add(quiz);
        }
        return quizzes;
    }
    public List<Quiz> getQuizByName(String name) throws SQLException {
        String st="SELECT * FROM quizes WHERE quiz_name like ?";
        PreparedStatement ps = con.prepareStatement(st);
        ps.setString(1, "%" + name + "%");
        ResultSet rs = ps.executeQuery();
        ArrayList<Quiz> quizzes = new ArrayList<>();
        while(rs.next()) {
            int quiz_id = rs.getInt("quiz_id");
            String quiz_name = rs.getString("quiz_name");
            String quiz_description = rs.getString("quiz_description");
            int user_id = rs.getInt("user_id");
            // Create quiz without questions for dropdown (more efficient)
            Quiz quiz = new Quiz(quiz_id, quiz_name, quiz_description, user_id, new ArrayList<>());
            quizzes.add(quiz);
        }
        return quizzes;
    }

}
