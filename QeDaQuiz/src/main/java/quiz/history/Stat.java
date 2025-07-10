package quiz.history;

import java.sql.Timestamp;

public class Stat {
    private int userId;
    private int quizId;
    private int points;
    private int maxPoints;
    private long time;
    private Timestamp last;
    private float avgScore;
    private float avgTime;
    private int attempts;
    public Stat(int userId, int quizId, int points, int maxPoints, long time, Timestamp last, float avgScore, float avgTime, int attempts) {
        this.userId = userId;
        this.quizId = quizId;
        this.points = points;
        this.maxPoints = maxPoints;
        this.time = time;
        this.last = last;
        this.avgScore = avgScore;
        this.avgTime = avgTime;
        this.attempts = attempts;
    }

    public int getUserId() {
        return userId;
    }
    public int getQuizId() {
        return quizId;
    }
    public int getPoints() {
        return points;
    }
    public int getMaxPoints() {
        return maxPoints;
    }
    public float getAvgScore() {
        return avgScore;
    }
    public float getAvgTime() {
        return avgTime;
    }
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }
    public int getAttempts() {
        return attempts;
    }
    public String getPercent(){
        int percent = points * 100 / maxPoints;
        String ans = Integer.toString(percent) + "%";
        return ans;
    }
    public long getTime() {
        return time;
    }
    public String getLast() {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(last);
        return date;
    }

}
