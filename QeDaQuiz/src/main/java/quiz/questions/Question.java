package quiz.questions;

import java.util.List;

public abstract class Question {
    protected String qustion;
    protected List<String> correct_answers;
    protected String type;
    public Question(String qustion, List<String> correct_answers, String type) {
        this.qustion = qustion;
        this.correct_answers = correct_answers;
        this.type = type;
    }
    public abstract int getPoint(List<String> answers);
}
