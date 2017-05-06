import server.OutputRunnable;
import server.RoomRunnable;
import server.ServerRunnable;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by SurfinBirb on 28.04.2017.
 */
public class CommandLine {

    private boolean off = true;
    private boolean paused = false;

    public void launch() throws Exception{
        BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
        String line;
        String[] arguments;
        ServerRunnable serverRunnable = ServerRunnable.getInstance();
        Thread serverThread = new Thread(serverRunnable);
        serverThread.setDaemon(true);
        Thread outputThread = new Thread(OutputRunnable.getInstance());
        outputThread.setDaemon(true);
        Thread roomThread = new Thread(RoomRunnable.getInstance());
        roomThread.setDaemon(true);

        while (true){

            if ( (line = in.readLine()) != null){
                arguments = line.split(" ");
                if (arguments[0].equals("!")){
                    if (arguments[1].equals("on") && off && !paused) {
                        serverThread.start();
                        roomThread.start();
                        outputThread.start();
                        off = false;
                        paused = false;
                        System.out.println("Server turned on");
                    }
                    /*if (arguments[1].equals("on") && off && paused) {
                        roomThread.run();
                        outputThread.run();
                        serverThread.run();
                        off = false;
                        paused = false;
                        System.out.println("Server turned on");
                    }
                    if (arguments[1].equals("shutdown") && !off  && !paused){
                        serverRunnable.shutdown();
                        serverThread.interrupt();
                        outputThread.interrupt();
                        roomThread.interrupt();
                        off = true;
                        paused = true;
                        System.out.println("Server turned off");
                    }*/
                    if (arguments[1].equals("status")){
                        System.out.println(off ? "Server is off" : "Server is on");
                    }
                    if (arguments[1].equals("exit")) {
                        System.exit(0);
                    }
                }
            }
            try {
                this.wait(500);
            } catch (Exception ignore){}
        }
    }
}
