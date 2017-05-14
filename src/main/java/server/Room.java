package server;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SurfinBirb on 22.04.2017.
 */
public class Room {

    private volatile Long roomId;
    private volatile Long creatorId;
    private volatile LinkedList<Message> messages;
    private volatile String roomName;
    private volatile List<Long> idList;

    public Long getCreatorId() {
        return creatorId;
    }

    public Room(Long creatorId,Long roomId, String roomName, List<Long> idList, LinkedList<Message> messages) {
        this.creatorId = creatorId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.idList = idList;
        this.messages = messages;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public LinkedList<Message> getMessages() {
        return messages;
    }
}
