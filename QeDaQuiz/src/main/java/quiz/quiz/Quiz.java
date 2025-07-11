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

    private int totalScore;
    private int quizId;
    private String quizName;
    private String quizDescription;
    private int userId;
    private List<Question> questions;
    private int gottenScore;
    private boolean isRandom;

    public Quiz(int quizId, String quizName, String quizDescription, int userId, List<Question> questions, boolean isRandom) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.userId = userId;
        this.questions = questions;
        this.totalScore = 0;
        for (Question question : questions) {
            this.totalScore += question.getMaxScore();
        }
        this.gottenScore = 0;
        this.isRandom = isRandom;
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

    public int getTotalScore() {
        return totalScore;
    }

    public int getGottenScore() {
        return gottenScore;
    }

    public void increaseGottenScore(int pt){
        gottenScore += pt;
    }

    public void setGottenScore(int gottenScore) {
        this.gottenScore = gottenScore;
    }

    public boolean isRandom() {
        return isRandom;
    }

}

