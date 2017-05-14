package server;

import java.util.TreeMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by SurfinBirb on 23.04.2017.
 */
public class Storage {
    private static volatile Storage instance;

    public Storage() {
        this.roomCreateRequests = new LinkedBlockingDeque<>();
        this.inputMessageQueue = new LinkedBlockingDeque<>();
        this.serviceMessages = new LinkedBlockingDeque<>();
        this.outputQueue = new LinkedBlockingDeque<>();
        this.errorMessages = new LinkedBlockingDeque<>();
        this.threadTreeMap = new TreeMap<>();
    }

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

    private volatile BlockingDeque<Room>            roomCreateRequests;
    private volatile BlockingDeque<Message>         inputMessageQueue;
    private volatile BlockingDeque<ServiceMessage>  serviceMessages;
    private volatile BlockingDeque<Packet>          outputQueue;
    private volatile BlockingDeque<String>          errorMessages;
    private volatile TreeMap<Long, SocketRunnable> threadTreeMap;

    public TreeMap<Long, SocketRunnable> getThreadTreeMap() {
        return threadTreeMap;
    }

    public BlockingDeque<Room>              getRoomCreateRequests() {
        return roomCreateRequests;
    }

    public BlockingDeque<Message>           getInputMessageQueue() {
        return inputMessageQueue;
    }

    public BlockingDeque<Packet>            getOutputQueue() {
        return outputQueue;
    }

    public BlockingDeque<String>            getErrorMessages() {
        return errorMessages;
    }

    public BlockingDeque<ServiceMessage>    getServiceMessages() {
        return serviceMessages;
    }

    public void storeThread(Long clientId, SocketRunnable socketRunnable){
        threadTreeMap.put(clientId,socketRunnable);
    }
}
