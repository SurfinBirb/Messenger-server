package server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created by SurfinBirb on 26.04.2017.
 */
public class ServerRunnable implements Runnable {
    private static volatile ServerRunnable instance;

    private ServerRunnable() {
        this.live = true;

    }

    public static ServerRunnable getInstance() {
        ServerRunnable localInstance = instance;
        if (localInstance == null) {
            synchronized (ServerRunnable.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ServerRunnable();
                }
            }
        }
        return localInstance;
    }

    private boolean live;

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isLive() {
        return live;
    }

    @Override
    public void run(){
        Storage storage = Storage.getInstance();
        try {

            Config config = ConfigReader.getInstance().getConfig();

            SSLServerSocketFactory sslserversocketfactory = createSSLContext().getServerSocketFactory(); //New server socket factory from context
            SSLServerSocket currentServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(config.serverConfiguration.port); //New ServerSocket
            currentServerSocket.setEnabledCipherSuites(currentServerSocket.getSupportedCipherSuites()); // Setting the cipher suites

            Socket currentSocket;

            while (live) { //For every new client

                currentSocket = currentServerSocket.accept(); //Get new socket
                if (currentSocket != null) { //If success
                    System.out.println(
                            "\nConnection established:\n    address = " + currentSocket.getInetAddress().toString() +
                            "\n    localPort = " + currentSocket.getLocalPort() +
                            "\n    port = " + currentSocket.getPort()
                    );
                    SocketRunnable socketRunnable = new SocketRunnable(currentSocket); //Create new SocketRunnable implements Runnable
                    Thread thread = new Thread(socketRunnable); //Create new thread
                    thread.setDaemon(true); //As daemon
                    thread.start(); //And launch it
                }
            }
        } catch (Exception e) {
            if ((e != null) || (e.getMessage() != null)) {
                String s = e.getMessage();
                System.out.println(s);
                storage.getErrorMessages().offerLast(s);
            }
        }
    }

    private SSLContext createSSLContext() throws Exception{

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src/main/java/config/keystore.jks"),"PraiseKekMahBoi".toCharArray());
        // Create key manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "PraiseKekMahBoi".toCharArray());
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        // Create trust manager
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        // Initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(keyManagers,  trustManagers, null);

        return sslContext;
    }

    /**
     * This method shutdowns the server
     * @throws Exception
     */
    public void shutdown() throws Exception {
        String shutdownMessage = new Packer().pack(
                new Packet("servicemessage",
                        null,
                        null,
                        null,
                        new ServiceMessage("server shutdown"),
                        null,
                        null
                )
        );
        Sender.getInstance().broadcast(shutdownMessage);
        for (SocketRunnable socketRunnable : Storage.getInstance().getThreadTreeMap().values()) {
            socketRunnable.closeSocket();
        }
        setLive(false);
    }

}
