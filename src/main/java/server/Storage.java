package server;

import java.util.HashMap;
import java.util.concurrent.BlockingDeque;

/**
 * Created by SurfinBirb on 23.04.2017.
 */
public class Storage {
    private static volatile Storage instance;

    public static Storage getInstance() {
        Storage localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Storage();
                }
            }
        }
        return localInstance;
    }

    private volatile BlockingDeque<Room>    roomCreateRequests;
    private volatile BlockingDeque<Message> inputMessageQueue;
    private volatile BlockingDeque<Packet>  outputQueue;
    private volatile BlockingDeque<String>  errorMessages;
    private volatile HashMap<Long, SocketThread>  threadHashMap;

    public HashMap<Long, SocketThread> getThreadHashMap() {
        return threadHashMap;
    }

    public BlockingDeque<Room> getRoomCreateRequests() {
        return roomCreateRequests;
    }


    public BlockingDeque<Message> getInputMessageQueue() {
        return inputMessageQueue;
    }

    public BlockingDeque<Packet> getOutputQueue() {
        return outputQueue;
    }

    public BlockingDeque<String> getErrorMessages() {
        return errorMessages;
    }

}