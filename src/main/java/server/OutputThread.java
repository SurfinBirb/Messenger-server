package server;

/**
 * Created by SurfinBirb on 27.04.2017.
 */
public class OutputThread {
    private static volatile OutputThread instance;

    public static OutputThread getInstance() {
        OutputThread localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new OutputThread();
                }
            }
        }
        return localInstance;
    }

    public void run() {
        Storage storage = Storage.getInstance();
        while (ServerThread.getInstance().isLive()){
            Packet packet = storage.getOutputQueue().poll();
            String xmlPacket = new Packer().pack(packet);
            try {
                if (packet.getType().equals("message") || packet.getType().equals("room")) {
                    for (Long id : packet.getRoom().getIdList()) {
                        Sender.getInstance().send(id, xmlPacket);
                    }
                }

                if (packet.getType().equals("servicemessage")){
                    Sender.getInstance().send(packet.getClientId(), xmlPacket);
                }
            } catch (Exception e) {
                storage.getErrorMessages().add(e.getMessage());
            }
        }
    }
}
