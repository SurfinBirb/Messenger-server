package server;

import java.util.List;

/**
 * Created by SurfinBirb on 22.04.2017.
 */
public class Room {

    private Long roomId;
    private Long creatorId;

    public Long getCreatorId() {
        return creatorId;
    }

    public Room(Long creatorId,Long roomId, String roomName, List<Long> idList) {
        this.creatorId = creatorId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.idList = idList;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    private String roomName;
    private List<Long> idList;

    public List<Long> getIdList() {
        return idList;
    }
}
