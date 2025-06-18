package quiz.quiz;

import Daos.QuizDao;
import quiz.questions.Question;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Quiz {

    private int quizId;
    private String quizName;
    private String quizDescription;
    private int userId;
    private List<Question> questions;

    public Quiz(int quizId, String quizName, String quizDescription, int userId, List<Question> questions) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.userId = userId;
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getQuizId() {
        return quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public int getUserId() {
        return userId;
    }

}

