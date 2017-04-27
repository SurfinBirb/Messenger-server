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

    /**
     * Рассылает пакет всем в комнате
     * @param room - room id
     * @param xmlPacket - String made by Packer class
     * @throws Exception
     */
    public void send(Room room, String xmlPacket) throws Exception{
        Storage storage = Storage.getInstance();
        for (Long id: room.getIdList()) {
            Socket socket = storage.getThreadHashMap().get(id).getSocket();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(xmlPacket);
            dataOutputStream.flush();
        }
    }

    /**
     * Посылает пакет клиенту()
     * @param id - client id
     * @param xmlPacket - String made by Packer class
     * @throws Exception
     */
    public void send(Long id, String xmlPacket) throws Exception{
        Storage storage = Storage.getInstance();
        Socket socket = storage.getThreadHashMap().get(id).getSocket();
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeUTF(xmlPacket);
        dataOutputStream.flush();
    }

    /**
     * Рассылает пакет всем, подключенным к серверу
     * @param xmlPacket - String made by Packer class
     * @throws Exception
     */
    public void broadcast(String xmlPacket){
        Storage.getInstance().getThreadHashMap().keySet().forEach(id -> {
            try {
                send(id, xmlPacket);
            } catch (Exception e) {
                Storage.getInstance().getErrorMessages().add(e.getMessage());
            }
        });
    }
}
