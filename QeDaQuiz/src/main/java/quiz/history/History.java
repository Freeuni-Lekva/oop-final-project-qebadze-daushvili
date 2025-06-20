package quiz.history;

import quiz.questions.Question;
import quiz.quiz.Quiz;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class History {
    private int userId;
    private Deque<Quiz> quizes;

    public History(int userId) {
        this.userId = userId;
        this.quizes = new ArrayDeque<Quiz>();
    }

    public Deque<Quiz> getQuizes() {
        return quizes;
    }

    public int getSize(){
        return quizes.size();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizes(Deque<Quiz> quizes) {
        this.quizes = quizes;
    }

    public void addQuiz(Quiz quiz) {
        quizes.addFirst(quiz);
    }

    public void removeQuiz() {
        quizes.removeLast();
    }

    public Quiz getQuiz(int ind) {
        Iterator<Quiz> iterator = quizes.iterator();
        Quiz ans = null;
        int count = 0;
        while (iterator.hasNext()) {
            Quiz current = iterator.next();
            count++;
            if (count == ind) {
                ans = current;
                break;
            }
        }
        return ans;
    }

}
