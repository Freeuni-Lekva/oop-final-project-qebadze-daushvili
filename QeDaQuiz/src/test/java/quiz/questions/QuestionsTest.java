package quiz.questions;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(1,question.getMaxScore());
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

    @Test
    public void testGetCorrectAnswers() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();
        correct_answers.add("Paris");
        wrong_answers.add("London");
        wrong_answers.add("Berlin");
        wrong_answers.add("Madrid");

        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Capital of France?", correct_answers, wrong_answers, "MULTIPLE_CHOICE");
        assertEquals(correct_answers, question.getCorrectAnswers());
    }

    @Test
    public void testGetWrongAnswers() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();
        correct_answers.add("Tokyo");
        wrong_answers.add("Seoul");
        wrong_answers.add("Beijing");
        wrong_answers.add("Bangkok");

        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Capital of Japan?", correct_answers, wrong_answers, "MULTIPLE_CHOICE");
        assertEquals(wrong_answers, question.getWrongAnswers());
    }

    @Test
    public void testGetCorrectAnswerIndex() {
        List<String> correct_answers = new ArrayList<>();
        List<String> wrong_answers = new ArrayList<>();
        correct_answers.add("Apple");
        wrong_answers.add("Banana");
        wrong_answers.add("Orange");
        wrong_answers.add("Grape");

        MultipleChoiceQuestion question = new MultipleChoiceQuestion("Which is red?", correct_answers, wrong_answers, "MULTIPLE_CHOICE");
        int index = question.get_correct_answer_index();
        assertTrue(index >= 0 && index < 4);
        assertEquals("Apple", question.get_possible_answers().get(index));
    }


}
