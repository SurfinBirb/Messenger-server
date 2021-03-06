package server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.TreeMap;

/**
 * Created by SurfinBirb on 23.04.2017.
 */
public class SocketRunnable implements Runnable {

    private Socket socket;
    private Long clientId;

    /**
     * SocketRunnable
     * При подключении клиента создается тред (из SocketRunnable), обрабатывающий InputStream сокета.
     * Принимает от клиента xml-структуру. Далее структура превращается в объект класса Packet, далее обрабатывающийся
     * @param socket - socket
     */
    public SocketRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        Storage storage = Storage.getInstance();
        WannabeSQLbd bd = WannabeSQLbd.getInstance();
        try {
            for (int i = 0; i <= 2; i++) {
                String xmlPacket = listen(socket.getInputStream());
                AuthData authData = new Unpacker().unpack(xmlPacket).getAuthData();
                System.out.println("\nLogin attempt:\n    login = " + authData.getLogin() + "; passwordHash = " + authData.getHash() + "\n");
                Long clientId = bd.getClientId(authData.getLogin());
                if (clientId != null){
                    if (authData.getHash().equals(bd.getIdPasswordHash(clientId))) {               //Сравнение хэшей паролей
                        this.clientId = clientId;
                        storage.storeThread(clientId, this);
                        TreeMap<Long, Room> roomMap = new TreeMap<>();
                        for (Long roomId : bd.getRoomIds(clientId)) roomMap.put(roomId, bd.getRoomById(roomId));
                        System.out.println("Success");
                        Sender.getInstance().send(clientId, new Packer().pack(
                                new Packet(
                                        "auth",
                                        null,
                                        null,
                                        null,
                                        null,
                                        new AuthData(
                                                null,
                                                null,
                                                clientId,
                                                true
                                        ),
                                        roomMap
                                )
                                )
                        );

                        while (!socket.isClosed()) {
                            Packet packet = new Unpacker().unpack(listen(socket.getInputStream()));
                            if (packet.getType().equals("message")) {
                                Sender.getInstance().send(bd.getRoomById(packet.getMessage().roomid), new Packer().pack(
                                        new Packet(
                                                "message",
                                                packet.getMessage(),
                                                null,
                                                packet.getClientId(),
                                                null,
                                                null,
                                                null
                                        )
                                ));
                            }
                            if (packet.getType().equals("roomInvitation")){
                                if(packet.getRoom().getCreatorId().equals(bd.getRoomById(packet.getRoom().getRoomId()).getCreatorId())) {
                                    Room room = bd.getRoomById(packet.getRoom().getRoomId());
                                    Long newClientId = packet.getClientId();
                                    if (!room.getIdList().contains(newClientId)) {
                                        room.getIdList().add(newClientId);
                                        packet = new Packet(
                                                "roomInvitation",
                                                null,
                                                room,
                                                null,
                                                null,
                                                null,
                                                null
                                        );
                                        xmlPacket = new Packer().pack(packet);
                                        Sender.getInstance().send(newClientId, xmlPacket); // TODO: 19.06.2017 добавить рассылку о добавлении нового лица в диалог
                                        System.out.println(room.getIdList());
                                    }
                                }
                            }
                            if (packet.getType().equals("room")) {
                                storage.getRoomCreateRequests().add(packet.getRoom());
                            }
                            if (packet.getType().equals("serviceMessage")) {
                                storage.getServiceMessages().add(packet.getServiceMessage());
                            }
                        }
                        break;
                    }
            }
                if (i == 3) {
                        xmlPacket = new Packer().pack(
                                new Packet(
                                        "auth",
                                        null,
                                        null,
                                        null,
                                        null,
                                        new AuthData(
                                                null,
                                                null,
                                                null,
                                                false
                                        ),
                                        null
                                )
                        );
                        System.out.println("\nPOST:\n" + xmlPacket + "\n");
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF(xmlPacket);
                        dataOutputStream.flush();
                    }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                if (e.getMessage().equals("Connection reset")) {
                    if (clientId != null) {
                        System.out.println("Client " + clientId + " disconnected\n");
                        storage.getThreadTreeMap().remove(clientId);
                    }
                } else {
                    e.printStackTrace();
                    storage.getErrorMessages().offerLast(e.getMessage());
                }
            }
        }
    }

    /**
     * @param socketInputStream - Поток вывода прослушиваемого сокета
     * @return <code>String</code> with XML structure
     * @throws Exception
     */
    private String listen(InputStream socketInputStream) throws Exception{

        DataInputStream dataInputStream = new DataInputStream(socketInputStream);
        String line = dataInputStream.readUTF();
        System.out.println("\nGET:\n" + line + "\n");
        return line;

    }

    public Socket getSocket() {
        return socket;
    }

    public Long getClientId() {
        return clientId;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

}
