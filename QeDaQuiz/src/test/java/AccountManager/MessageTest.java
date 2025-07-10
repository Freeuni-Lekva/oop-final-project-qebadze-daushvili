package AccountManager;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testMessageConstructorAndGetters() throws NoSuchAlgorithmException {
        Account sender = new Account("pass", "user", "photo");
        Account receiver = new Account("pass", "user", "photo");

        String content = "Hello World";
        String type = "text";
        int quizId = 123;

        Message message = new Message(content, type, sender, receiver, quizId);

        assertEquals(content, message.getContent());
        assertEquals(type, message.getType());
        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(quizId, message.getQuizId());
    }

    @Test
    public void testMessageWithDifferentValues() throws NoSuchAlgorithmException {
        Account sender = new Account("pass", "user", "photo");
        Account receiver = new Account("pass", "user", "photo");

        String content = "Quiz invitation";
        String type = "quiz";
        int quizId = 456;

        Message message = new Message(content, type, sender, receiver, quizId);

        assertEquals(content, message.getContent());
        assertEquals(type, message.getType());
        assertEquals(sender, message.getSender());
        assertEquals(receiver, message.getReceiver());
        assertEquals(quizId, message.getQuizId());
    }
}