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
        try {
        AuthData authData = new Unpacker().unpack(listen(socket.getInputStream())).getAuthData();
        this.id = bd.getLoginToId().get(authData.getLogin());
        if (authData.getHash().equals(bd.getIdPasswordHash().get(id))) {  //Сравнение хэшей паролей
            storage.getThreadHashMap().put(bd.getLoginToId().get(authData.getLogin()), this);
            while (!socket.isClosed()) {
                Packet packet = new Unpacker().unpack(listen(socket.getInputStream()));
                if (packet.getType().equals("Message")) {
                    storage.getInputMessageQueue().add(packet.getMessage());
                }
                if (packet.getType().equals("Room")) {
                    storage.getRoomCreateRequests().add(packet.getRoom());
                }
                if (packet.getType().equals("ServiceMessage")) ;
            }
        }
        } catch (Exception e){storage.getErrorMessages().add(e.getMessage());}
    }


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

}
