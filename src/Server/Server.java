package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A szerver megvalósításáért felleős osztály: egy ExecutorService segítségével több kliens csatlakozását teszi
 * lehetőve az osztály. A clientHandler fogja foylamatosan hozzátenni az itt létrehozott "clientsWithUsername"
 * Map-hez a legújabban csatlakozott user-t.
 */
public class Server {
    private static final int PORT = 8091;
    public static Map<String, ClientHandler> clientsWithUsername = new HashMap<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(20);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        while (true)
        {
            Socket client = listener.accept();
            ClientHandler clientThread = new ClientHandler(client);
            pool.execute(clientThread);
        }

    }
}