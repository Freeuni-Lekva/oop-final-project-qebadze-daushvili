package AccountManager;

public class Message {
    private String content;
    private String type;
    private Account sender;
    private Account receiver;
    private int quizId;
    public Message(String content, String type, Account sender, Account receiver, int quizId) {
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
    public Account getSender() {
        return sender;
    }
    public Account getReceiver() {
        return receiver;
    }
    public int getQuizId() {
        return quizId;
    }





}
