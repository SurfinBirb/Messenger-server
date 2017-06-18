package server;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by SurfinBirb on 27.04.2017.
 */
public class Sender {
    private static volatile Sender instance;

    public static Sender getInstance() {
        Sender localInstance = instance;
        if (localInstance == null) {
            synchronized (Sender.class) {
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
           if(storage.getThreadTreeMap().containsKey(id)){
                Socket socket = storage.getThreadTreeMap().get(id).getSocket();
                if (!socket.isClosed()) {
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    try {
                        dataOutputStream.writeUTF(xmlPacket);
                        dataOutputStream.flush();
                        System.out.println("\nPOST:\n" + xmlPacket + "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        storage.getThreadTreeMap().remove(id);
                    }
                } else storage.getThreadTreeMap().remove(id);
            }
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
        if(storage.getThreadTreeMap().get(id) != null){
            DataOutputStream dataOutputStream = new DataOutputStream(storage.getThreadTreeMap().get(id).getSocket().getOutputStream());
            dataOutputStream.writeUTF(xmlPacket);
            dataOutputStream.flush();
            System.out.println("\nPOST:\n" + xmlPacket + "\n");
        }
    }

    /**
     * Рассылает пакет всем, подключенным к серверу
     * @param xmlPacket - String made by Packer class
     * @throws Exception
     */
    public void broadcast(String xmlPacket){
        Storage.getInstance().getThreadTreeMap().keySet().forEach(id -> {
            try {
                send(id, xmlPacket);
            } catch (Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    Storage.getInstance().getErrorMessages().offerLast(e.getMessage());
                }
            }
        });
    }
}
