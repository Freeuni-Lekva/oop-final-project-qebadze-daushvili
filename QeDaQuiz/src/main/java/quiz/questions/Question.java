package quiz.questions;

import java.util.List;

public abstract class Question {

    private int questionId;
    protected String question;
    protected List<String> correct_answers;
    protected String type;

    public Question(String question, List<String> correct_answers, String type) {
        this.question = question;
        this.correct_answers = correct_answers;
        this.type = type;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getType() {
        return type;
    }

    public String getPrompt() {
        return question;
    }

    public int getMaxScore() {
        return 1;
    }

    public abstract int getPoint(List<String> answers);
}
