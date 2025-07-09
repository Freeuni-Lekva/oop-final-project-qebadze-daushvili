package quiz.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MultipleChoiceQuestion extends Question{
    protected List<String> wrong_answers;
    private List<String> all_answers;
    public MultipleChoiceQuestion(String question, List<String> correct_answers, List<String> wrong_answers, String type) {
        super(question, correct_answers, type);
        this.wrong_answers = wrong_answers;
        all_answers = new ArrayList<>();
        all_answers.addAll(correct_answers);
        all_answers.addAll(wrong_answers);
        Collections.shuffle(all_answers);
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
        return all_answers;
    }

    public int get_correct_answer_index() {
        for (int i = 0; i < all_answers.size(); i++) {
            if (all_answers.get(i).equals(correct_answers.get(0))) {
                return i;
            }
        }
        return -1;
    }

    public List<String> getWrongAnswers() {
        return wrong_answers;
    }

}
