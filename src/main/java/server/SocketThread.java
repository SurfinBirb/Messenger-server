package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by SurfinBirb on 23.04.2017.
 */
public class SocketThread implements Runnable {

    private Socket socket;
    private Long id;

    /**
     * SocketThread
     * @param socket - socket
     */
    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        Storage storage = Storage.getInstance();
        WannabeSQLbd bd = WannabeSQLbd.getInstance();
        int attempts = 0;
        try {
            for (int i = 0; i <= 2; i++) {
                AuthData authData = new Unpacker().unpack(listen(socket.getInputStream())).getAuthData();
                this.id = bd.getLoginToId().get(authData.getLogin());
                if (authData.getHash().equals(bd.getIdPasswordHash().get(id))) {                //Сравнение хэшей паролей
                    storage.getThreadHashMap().put(bd.getLoginToId().get(authData.getLogin()), this);
                    Sender.getInstance().send(id, new Packer().pack(
                            new Packet(
                                    "auth",
                                    null,
                                    null,
                                    null,
                                    null,
                                    new AuthData(
                                            null,
                                            null,
                                            id,
                                            true)
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
                }
                attempts++;
                if (attempts == 3){
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(new Packer().pack(
                            new Packet(
                                    "auth",
                                    null,
                                    null,
                                    null,
                                    null,
                                    new AuthData(
                                            authData.getLogin(),
                                            authData.getHash(),
                                            null,
                                            false
                                    )
                            )
                    )
                    );
                }
            }
        } catch (Exception e){storage.getErrorMessages().add(e.getMessage());}
    }

    /**
     * @param socketInputStream - Поток вывода прослушиваемого сокета
     * @return <code>String</code> with XML structure
     * @throws Exception
     */
    private String listen(InputStream socketInputStream) throws Exception{
        int c;
        StringBuilder stringBuilder = new StringBuilder();
        while ( (c = socketInputStream.read()) != -1){
            stringBuilder.append((char) c);
        }
        return stringBuilder.toString();
    }

    public Socket getSocket() {
        return socket;
    }

    public Long getId() {
        return id;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

}
