package server;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by SurfinBirb on 27.04.2017.
 */
public class Sender {
    private static volatile Sender instance;

    public static Sender getInstance() {
        Sender localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Sender();
                }
            }
        }
        return localInstance;
    }

    public void send(Room room, String xmlPacket) throws Exception{
        Storage storage = Storage.getInstance();
        for (Long id: room.getIdList()) {
            Socket socket = storage.getThreadHashMap().get(id).getSocket();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(xmlPacket);
            dataOutputStream.flush();
        }
    }
}
