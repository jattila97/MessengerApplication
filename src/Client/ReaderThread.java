package Client;

import Client.JavaFX.MainController;
import Common.Message;
import Common.Util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReaderThread implements Runnable{

    private static String userName;
    private static String[] users;
    private final DataInputStream inputStream;
    public static boolean loginAccepted = false;
    public static List<String> messList = new ArrayList<>();
    public static List<String> messFromTo = new ArrayList<>();

    /**
     * Az olvasásért felelős szál; megkapja a Client osztálytól a "serverSocket" inputstream-jét
     */
    public ReaderThread(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    /**
     * A runnable interface megvalósítandó metódusa.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Message msg = Util.readMessage(inputStream);
                if (msg != null)
                {
                    System.out.println(msg.getContent());
                    analyzeMessage(msg);
                }
            }
        } catch (SocketException ex) {
            System.out.println("Cannot connect to Server!");
        }
        catch (IOException ex) {
            System.out.println("Error reading from server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * A megkapott üzenteket a típusuk alapján kezeli a szál. A SENDUSERS-nél a user-eket szétbontja ";" szerint,
     * mivel azok egyben érkeznek az üzenet "content"-jében. Valamint a TEXT esetén a messageList-be beletesszük
     * az üzenetet, amit később a MainController osztály fog felhasználni azok megjelenítésére.
     */
    public void analyzeMessage(Message message)
    {
        switch (message.getType())
        {
            case SENDUSERS:
                users = message.getContent().split(";");
                break;
            case ERRORPASS:
                break;
            case ERRORUSER:
                break;
            case ACCEPTED:
                loginAccepted = true;
                userName = message.getContent();
                break;
            case TEXT:
                String message1 = message.getSourceUsername() + ": " + message.getContent();
                messList.add(message1);
                messFromTo.add(message.getSourceUsername() + "-" + message.getToWho());
                break;
            case SENDHISTORY:
                historyAnalyzer(message);
        }
    }

    public static String[] getUsers() {
        return users;
    }

    private void historyAnalyzer(Message message)
    {
        int length = message.getContentArray().length;
        int i = -1;
        while (i<length-1)
        {
            i++;
            if (message.getContentArray()[i] == null)
            {
                break;
            }
            String message1 = message.getSourceUsernameArray()[i] + ": " + message.getContentArray()[i];
            messList.add(message1);
            messFromTo.add(message.getSourceUsernameArray()[i] + "-" + message.getToWhoArray()[i]);
        }
    }

    public static String getUserName() {
        return userName;
    }
}
