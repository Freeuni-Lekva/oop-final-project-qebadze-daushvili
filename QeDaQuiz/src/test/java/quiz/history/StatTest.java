package quiz.history;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;

public class StatTest {

    @Test
    public void testStatConstructorAndGetters() {
        int userId = 101;
        int quizId = 202;
        int points = 85;
        int maxPoints = 100;
        long time = 1500L;
        Timestamp last = new Timestamp(System.currentTimeMillis());
        float avgScore = 78.5f;
        float avgTime = 1200.75f;
        int attempts = 3;

        Stat stat = new Stat(userId, quizId, points, maxPoints, time, last, avgScore, avgTime, attempts);

        assertEquals(userId, stat.getUserId());
        assertEquals(quizId, stat.getQuizId());
        assertEquals(points, stat.getPoints());
        assertEquals(maxPoints, stat.getMaxPoints());
        assertEquals(time, stat.getTime());
        assertEquals(avgScore, stat.getAvgScore(), 0.001f);
        assertEquals(avgTime, stat.getAvgTime(), 0.001f);
        assertEquals(attempts, stat.getAttempts());
    }

    @Test
    public void testGetPercent() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Stat stat = new Stat(1, 1, 85, 100, 1000L, timestamp, 80.0f, 1200.0f, 2);

        String percent = stat.getPercent();
        assertEquals("85%", percent);
    }

    @Test
    public void testGetPercentWithDifferentValues() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Stat stat = new Stat(1, 1, 75, 100, 1000L, timestamp, 80.0f, 1200.0f, 2);

        String percent = stat.getPercent();
        assertEquals("75%", percent);
    }

    @Test
    public void testGetPercentWithZeroPoints() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Stat stat = new Stat(1, 1, 0, 100, 1000L, timestamp, 80.0f, 1200.0f, 2);

        String percent = stat.getPercent();
        assertEquals("0%", percent);
    }

    @Test
    public void testGetLast() {
        Timestamp timestamp = Timestamp.valueOf("2024-03-15 14:30:45.123");
        Stat stat = new Stat(1, 1, 85, 100, 1000L, timestamp, 80.0f, 1200.0f, 2);

        String lastFormatted = stat.getLast();
        assertEquals("2024-03-15 14:30", lastFormatted);
    }

    @Test
    public void testGetLastWithDifferentTimestamp() {
        Timestamp timestamp = Timestamp.valueOf("2023-12-25 09:15:30.456");
        Stat stat = new Stat(2, 3, 90, 100, 2000L, timestamp, 85.0f, 1500.0f, 5);

        String lastFormatted = stat.getLast();
        assertEquals("2023-12-25 09:15", lastFormatted);
    }

    @Test
    public void testCompleteScenario() {
        int userId = 999;
        int quizId = 888;
        int points = 42;
        int maxPoints = 50;
        long time = 3600L;
        Timestamp last = Timestamp.valueOf("2024-01-01 12:00:00.000");
        float avgScore = 84.2f;
        float avgTime = 2400.5f;
        int attempts = 7;

        Stat stat = new Stat(userId, quizId, points, maxPoints, time, last, avgScore, avgTime, attempts);

        assertEquals(userId, stat.getUserId());
        assertEquals(quizId, stat.getQuizId());
        assertEquals(points, stat.getPoints());
        assertEquals(maxPoints, stat.getMaxPoints());
        assertEquals(time, stat.getTime());
        assertEquals(avgScore, stat.getAvgScore(), 0.001f);
        assertEquals(avgTime, stat.getAvgTime(), 0.001f);
        assertEquals(attempts, stat.getAttempts());
        assertEquals("84%", stat.getPercent());
        assertEquals("2024-01-01 12:00", stat.getLast());
    }
}