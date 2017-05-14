package server;

import java.io.*;
import java.net.Socket;
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
        int attempts = 0;
        try {
            for (int i = 0; i <= 2; i++) {
                String xmlPacket = listen(socket.getInputStream());
                AuthData authData = new Unpacker().unpack(xmlPacket).getAuthData();
                System.out.println("\nLogin attempt:\n    login = " + authData.getLogin() + "; passwordHash = " + authData.getHash() + "\n");
                Long clientId = bd.getClientId(authData.getLogin());
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
                        if (packet.getType().equals("Message")) {
                            storage.getInputMessageQueue().add(packet.getMessage());
                        }
                        if (packet.getType().equals("Room")) {
                            storage.getRoomCreateRequests().add(packet.getRoom());
                        }
                        if (packet.getType().equals("ServiceMessage")) {
                            storage.getServiceMessages().add(packet.getServiceMessage());
                        }
                    }
                    break;
                } else {
                    attempts++;
                    if (attempts == 3) {
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
                        PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
                        outputWriter.println(xmlPacket);
                    }
                }
            }
        } catch (Exception e){
            if (e.getMessage() != null) {
                e.printStackTrace();
                storage.getErrorMessages().offerLast(e.getMessage());
            }
        }
    }

    /**
     * @param socketInputStream - Поток вывода прослушиваемого сокета
     * @return <code>String</code> with XML structure
     * @throws Exception
     */
    private String listen(InputStream socketInputStream) throws Exception{

        /**Вариант 1*/

//        int c;
//        StringBuilder stringBuilder = new StringBuilder();
//        while ( (c = socketInputStream.read()) != -1){
//            stringBuilder.append((char) c);
//        }
//        stringBuilder.setCharAt(0, '<');
//        System.out.println("\nGET:\n" + stringBuilder.toString() + "\n");
//        return stringBuilder.toString();

        /**Вариант 2*/

//        int c;
//        StringBuilder stringBuilder = new StringBuilder();
//        while ( (c = socketInputStream.read()) != -1){
//            stringBuilder.append((char) c);
//        }
//        stringBuilder.deleteCharAt(0).deleteCharAt(0);
//        System.out.println("\nGET:\n" + stringBuilder.toString() + "\n");
//        return stringBuilder.toString();


        /**Вариант 3: не работает*/

//        DataInputStream dataInputStream = new DataInputStream(socketInputStream);
//        PushbackInputStream pushbackInputStream = new PushbackInputStream(socketInputStream);
//        StringBuilder stringBuilder = new StringBuilder();
//        String line = null;
//        int c;
//        while ((c = pushbackInputStream.read()) != -1){
//            pushbackInputStream.unread(c);
//            line = dataInputStream.readUTF();
//            stringBuilder.append(line);
//        }
//        System.out.println("\nGET:\n" + stringBuilder.toString() + "\n");
//        return stringBuilder.toString();

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
