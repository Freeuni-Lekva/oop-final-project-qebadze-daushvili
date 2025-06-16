package quiz.questions;

import java.util.List;

public class ResponseQuestion extends Question {
    public ResponseQuestion(String qustion, List<String> correct_answers, String type) {
        super(qustion, correct_answers, type);
    }

    @Override
    public int getPoint(List<String> answers) {
        String ans=answers.get(0);
        ans=ans.toLowerCase();
        for(int i=0;i<this.correct_answers.size();i++){
            String to_check=this.correct_answers.get(i).toLowerCase();
            if(to_check.equals(ans)){
                return 1;
            }
        }
        return 0;
    }
}
