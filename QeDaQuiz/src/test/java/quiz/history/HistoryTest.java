package quiz.history;

import org.junit.Before;
import org.junit.Test;
import quiz.questions.Question;
import quiz.questions.ResponseQuestion;
import quiz.quiz.Quiz;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HistoryTest {
    private History hist;
    private List<Question> mockQuestions;
    private Question mockQuestion;
    private Quiz quiz;
    private Deque<Quiz> quizes;
    @Before
    public void setUp() throws Exception {
        hist=new History(1);
        mockQuestions = new ArrayList<Question>();
        mockQuestion=new ResponseQuestion("mock", null, "mock");
        mockQuestions.add(mockQuestion);
        quiz=new Quiz(1, "quiz1", "mock", 1, mockQuestions );
        quizes = new ArrayDeque<Quiz>();
        quizes.add(quiz);
    }
    @Test
    public void set_get_user_test(){
        hist.setUserId(2);
        assertEquals(2, hist.getUserId());
    }
    @Test
    public void add_get_remove_quiz_test(){
        hist.addQuiz(quiz);
        hist.addQuiz(quiz);
        Quiz tmp=hist.getQuiz(2);
        assertEquals(quiz.getQuizName(),tmp.getQuizName());
        hist.removeQuiz();
        assertEquals(1, hist.getSize());
    }

    @Test
    public void get_set_quizes_test(){
        hist.setQuizes(quizes);
        assertEquals(1, hist.getQuizes().size());
    }
}
