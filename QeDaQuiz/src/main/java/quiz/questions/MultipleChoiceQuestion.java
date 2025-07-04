package quiz.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultipleChoiceQuestion extends Question{
    protected List<String> wrong_answers;
    public MultipleChoiceQuestion(String question, List<String> correct_answers, List<String> wrong_answers, String type) {
        super(question, correct_answers, type);
        this.wrong_answers = wrong_answers;
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

    public List<String> get_possible_answers() {
        List<String> answers=new ArrayList<String>();
        answers.addAll(this.wrong_answers);
        answers.addAll(this.correct_answers);
        Collections.shuffle(answers);
        return answers;
    }

    public List<String> getWrongAnswers() {
        return wrong_answers;
    }

}
