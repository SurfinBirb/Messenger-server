package server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created by SurfinBirb on 26.04.2017.
 */
public class ServerThread implements Runnable {

    public void run(){
        Storage storage = Storage.getInstance();
        try {

            Config config = ConfigReader.getInstance().getConfig();

            SSLServerSocketFactory sslserversocketfactory = createSSLContext().getServerSocketFactory(); //New server socket factory from context
            SSLServerSocket currentServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(config.serverConfiguration.port); //New ServerSocket
            currentServerSocket.setEnabledCipherSuites(currentServerSocket.getSupportedCipherSuites()); // Setting the cipher suites

            Socket currentSocket;

            while (true) { //For every new client

                currentSocket = currentServerSocket.accept(); //Get new socket
                if (currentSocket != null) { //If success
                    SocketThread socketThread = new SocketThread(currentSocket); //Create new SocketThread implements Runnable
                    Thread thread = new Thread(socketThread); //Create new thread
                    thread.setDaemon(true); //As daemon
                    thread.start(); //And launch it
                }
            }
        } catch (Exception e) {
            storage.getErrorMessages().add(e.getMessage());
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

}
