package server;

/**
 * Created by SurfinBirb on 22.04.2017.
 */
public class Packet {
    private String type;
    private Message message;
    private Room room;
    private ServiceMessage serviceMessage;
    private AuthData authData;

    public Packet(String type, Message message, Room room, ServiceMessage serviceMessage, AuthData auth) {
        this.type = type;
        this.message = message;
        this.room = room;
        this.serviceMessage = serviceMessage;
        this.authData = auth;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public String getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public Room getRoom() {
        return room;
    }

    public ServiceMessage getServiceMessage() {
        return serviceMessage;
    }
}
