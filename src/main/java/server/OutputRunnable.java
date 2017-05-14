package server;

/**
 * Created by SurfinBirb on 27.04.2017.
 */
public class OutputRunnable implements Runnable {
    private static volatile OutputRunnable instance;

    public static OutputRunnable getInstance() {
        OutputRunnable localInstance = instance;
        if (localInstance == null) {
            synchronized (Storage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new OutputRunnable();
                }
            }
        }
        return localInstance;
    }

    public void run() {
        Storage storage = Storage.getInstance();
        try {
            while (ServerRunnable.getInstance().isLive()) {
                Packet packet = storage.getOutputQueue().poll();
                while (packet != null) {
                    String xmlPacket = new Packer().pack(packet);
                        if (packet.getType().equals("message") || packet.getType().equals("room")) {
                            Sender.getInstance().send(packet.getRoom(), xmlPacket);
                        }

                        if (packet.getType().equals("servicemessage")) {
                            Sender.getInstance().send(packet.getClientId(), xmlPacket);
                        }
                    packet = storage.getOutputQueue().poll();
                    }
                this.wait(500);
                }
        } catch (Exception e) {
            if (e != null) {
                if(e.getMessage() != null) {
                    storage.getErrorMessages().offerLast(e.getMessage());
                }
            }
        }
    }
}
