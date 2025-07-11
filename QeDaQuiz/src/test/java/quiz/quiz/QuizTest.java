package quiz.quiz;

import org.junit.Test;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class QuizTest {

    @Test
    public void quizTest() {
        List<Question> questions = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        answer.add("Messi");
        ResponseQuestion mock1 = new ResponseQuestion("G.O.A.T.?", answer, "RESPONSE");
        ResponseQuestion mock2 = new ResponseQuestion("8 golden balls", answer, "RESPONSE");

        questions.add(mock1);
        questions.add(mock2);
        Quiz quiz = new Quiz(1, "football", "quiz about best footballer",5, questions, false);
        assertEquals(2, quiz.getTotalScore());
        assertEquals(0, quiz.getGottenScore());
        quiz.increaseGottenScore(1);
        assertEquals(1, quiz.getGottenScore());
        quiz.setGottenScore(2);
        assertEquals(2, quiz.getGottenScore());
        assertEquals(quiz.getQuizId(), 1);
        assertEquals(quiz.getQuizName(), "football");
        assertEquals(quiz.getQuizDescription(), "quiz about best footballer");
        assertEquals(quiz.getUserId(), 5);
        assertEquals(quiz.getQuestions().size(), 2);
        quiz.setQuizId(2);
        assertEquals(quiz.getQuizId(), 2);
        assertFalse(quiz.isRandom());
    }
}
