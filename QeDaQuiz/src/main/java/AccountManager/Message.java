package AccountManager;

public class Message {
    private String content;
    private String type;
    private int sender;
    private int receiver;
    private int quizId;
    public Message(String content, String type, int sender, int receiver, int quizId) {
        this.content=content;
        this.type=type;
        this.sender=sender;
        this.receiver=receiver;
        this.quizId=quizId;
    }
    public String getContent() {
        return content;
    }
    public String getType() {
        return type;
    }
    public int getSender() {
        return sender;
    }
    public int getReceiver() {
        return receiver;
    }
    public int getQuizId() {
        return quizId;
    }

}
