package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8091;
    public static DataOutputStream outputStream;

    /**
     * A kliens itt hozza létre a socket-et, amivel csatlakozik a szerverre, valamint
     * itt indítja el az adatok olvasásához szükséges szálat, amit Daemon-nak állítok
     * be, hogy a Main osztályban a launch() leállása után ez a szál is leálljon.
     */

    public Client() throws IOException {
        Socket serverSocket = new Socket(SERVER_IP,SERVER_PORT);
        outputStream = new DataOutputStream(serverSocket.getOutputStream());
        ReaderThread readerThread = new ReaderThread(serverSocket.getInputStream());
        Thread re = new Thread(readerThread);
        re.setDaemon(true);
        re.start();
    }
}
