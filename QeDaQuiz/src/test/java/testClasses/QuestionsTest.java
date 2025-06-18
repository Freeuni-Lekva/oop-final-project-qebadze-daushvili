package testClasses;

import org.junit.Test;
import quiz.questions.MultipleChoiceQuestion;
import quiz.questions.ResponseQuestion;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuestionsTest {

    @Test
    public void testMultipleChoiceQuestion() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();
        List<String> wrong_answers1 = new ArrayList<>();
        List<String> all_answers = new ArrayList<>();

        correct_answers.add("Red");
        wrong_answers.add("Yellow");
        wrong_answers.add("Blue");
        wrong_answers.add("Green");
        wrong_answers1.add("Yellow");

        MultipleChoiceQuestion question = new MultipleChoiceQuestion("What color is your Bugatti?", correct_answers, wrong_answers, "MULTIPLE_CHOICE");
        assertEquals(question.getPoint(correct_answers), 1);
        assertEquals(question.getPoint(wrong_answers1), 0);

        assertEquals(question.get_possible_answers().size(), 4);
    }

    @Test
    public void testMultipleChoiceQuestion2() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();
        correct_answers.add("Willis");
        wrong_answers.add("Pitt");
        wrong_answers.add("Norton");
        wrong_answers.add("Kilmer");

        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Bruce ...?", correct_answers, wrong_answers, "MULTIPLE_CHOICE");

        assertEquals(question.getPoint(correct_answers), 1);
        assertEquals(question.getPoint(wrong_answers), 0);
        assertEquals(question.getPrompt(), "Bruce ...?");
        assertEquals(question.getType(), "MULTIPLE_CHOICE");

        question.setQuestionId(1);
        assertEquals(question.getQuestionId(), 1);
    }

    @Test
    public void testResponseQuestion1() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();

        correct_answers.add("Inception");
        correct_answers.add("Following");
        wrong_answers.add("Harry Potter");

        ResponseQuestion resp = new ResponseQuestion("Nolan Film: ", correct_answers, "RESPONSE");

        List<String> answer = new ArrayList<>();
        answer.add("Inception");
        assertEquals(resp.getPoint(answer), 1);

        assertEquals(resp.getPoint(wrong_answers), 0);
    }

}
