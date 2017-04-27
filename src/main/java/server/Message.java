package server;

/**
 * Created by SurfinBirb on 21.04.2017.
 */
public class Message {
    public long roomid;
    public long timestamp;
    public long sender;
    public String text;

    public Message(long roomid, long timestamp, long sender, String text) {
        this.roomid = roomid;
        this.timestamp = timestamp;
        this.sender = sender;
        this.text = text;
    }
}
