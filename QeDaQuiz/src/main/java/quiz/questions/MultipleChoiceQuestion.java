package quiz.questions;

import java.util.List;

public class MultipleChoiceQuestion extends Question{

    public MultipleChoiceQuestion(String qustion, List<String> correct_answers, String type) {
        super(qustion, correct_answers, type);
    }

    @Override
    public int getPoint(List<String> answers) {
        String s1=answers.get(0).toLowerCase();
        String s2=this.correct_answers.get(0).toLowerCase();
        if(s1.equals(s2)){
            return 1;
        }
        return 0;
    }
}
