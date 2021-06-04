package Server;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Arrays;
import Common.Message;
import Common.MessageType;
import Common.Util;

public class ClientHandler implements Runnable {
    private final Socket client;
    private DataOutputStream outputStream;

    /**
     * Minden egyes user-nek (socket-nek) külön szálat nyit a szerver egy ilyen Runnable interfészt megvalósító
     * clientHandler szál képében.
     */

    public ClientHandler(Socket clientSocket) {
        this.client = clientSocket;
    }

    @Override
    public void run() {
        try {
            this.outputStream = new DataOutputStream(client.getOutputStream());
            DataInputStream inputStream = new DataInputStream(client.getInputStream());

            Util.sendMessage(usersFromDBMessage(), outputStream);

            while (true) {
                Message msg = Util.readMessage(inputStream);
                if (msg != null) {
                    analyzeMessage(msg);
                    if (msg.getType() == MessageType.LOGOUT) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("User exit without logout!");
        }

    }

    /**
     * A beérkezett üzenetek típusának megfelelően reagál a szerver: REGISTER esetén berakja a suer-t az sqlite
     * adatbázisba. LOGIN esetén ellenőrzi a megfelelő jelszót az adatbázisból. TEXT esetén a megfelelő kliensnek
     * továbbküldi az üzenetet. LOGOUT esetén pedig kiveszi a user-eket és a hozzájuk tartozó clientHandler objektumokat
     * tartalmazó Map-ből a kijelentkezett user-t.
     */
    private void analyzeMessage(Message message) throws IOException {
        String url = "jdbc:sqlite:src/Server/chat.db";
        try (Connection connection = DriverManager.getConnection(url)) {
            switch (message.getType()) {
                case REGISTER:
                    System.out.println("register in server");
                    String[] split = message.getContent().split(";");
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (email, username, password) VALUES (" + "\"" + split[0] + "\"" + "," + "\"" + split[1] + "\"" + "," + "\"" + split[2] + "\"" + ");")) {
                        statement.execute();
                    }
                    sendUsers();
                    createUserTable(connection, message.getSourceUsername());
                    break;
                case LOGIN:
                    String username = message.getSourceUsername();
                    String password = message.getContent();
                    String[] usersString = new String[5];
                    String[] passString = new String[5];
                    try (PreparedStatement statement = connection.prepareStatement("SELECT username, password FROM users")) {
                        ResultSet usersAndPasswords = statement.executeQuery();
                        int i = -1;
                        while (usersAndPasswords.next()) {
                            i++;
                            if (i > usersString.length - 1) {
                                usersString = Arrays.copyOf(usersString, usersString.length * 2);
                                passString = Arrays.copyOf(passString, passString.length * 2);
                            }
                            usersString[i] = usersAndPasswords.getString("username");
                            passString[i] = usersAndPasswords.getString("password");
                        }
                    }


                    int i = 0;
                    boolean userFound = false;
                    Message message1 = null;
                    while (i < usersString.length) {
                        if (usersString[i] != null && usersString[i].equals(username)) {
                            userFound = true;
                            if (passString[i].equals(password)) {
                                message1 = Message.accepted(usersString[i]);
                                Server.clientsWithUsername.put(username, this);
                                sendHistory(connection, message);
                            } else {
                                message1 = Message.errorPass();
                            }
                        }
                        i++;
                    }
                    if (!userFound) {
                        message1 = Message.errorUser();
                    }
                    Util.sendMessage(message1, outputStream);
                    break;
                case ERROR:
                case TEXT:
                    String userTo = message.toWho;
                    String username1 = message.getSourceUsername();
                    String messageCont = message.getContent();
                    System.out.println("username: " + username1 + "content: " + messageCont);
                    ClientHandler clientTo = Server.clientsWithUsername.get(userTo);
                    if (clientTo != null) {
                        Util.sendMessage(message, clientTo.outputStream);
                        System.out.println("Text in server");
                        putTextInDB(connection,message);
                    } else {
                        System.out.println("User not present");
                        break;
                    }
                    break;
                case LOGOUT:
                    System.out.println("logout in server");
                    Server.clientsWithUsername.remove(message.getSourceUsername());
                    break;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static String[] usersString()
    {
        String[] usersString1 = new String[5];
        String url = "jdbc:sqlite:src/Server/chat.db";
        try (Connection connection = DriverManager.getConnection(url))
        {
            try (PreparedStatement statement = connection.prepareStatement("SELECT username FROM users"))
            {
                ResultSet users = statement.executeQuery();
                int i = -1;
                while (users.next()) {
                    i++;
                    if (i > usersString1.length - 1) {
                        usersString1 = Arrays.copyOf(usersString1, usersString1.length * 2);
                    }
                    usersString1[i] = users.getString("username");
                }
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return usersString1;
    }

    private static Message usersFromDBMessage()
    {
        String[] users = usersString();
        return Message.sendUsers(users);
    }

    /**
     * A user-ek elküldéséhez szükséges metódus: minden user-nek elküldi az aktuális user-eket tartalmazó userString
     * String tömbből a Message osztályban lévő sendUsers metódus segítségével a user-ek nevéből álló, azokat
     * egymással ";"-vel elválasztott üzenetet.
     */
    public void sendUsers()
    {
        for (int i = 0; i<usersString().length ;i++)
        {
            if (usersString()[i]!=null)
            {
                ClientHandler clientTo = Server.clientsWithUsername.get(usersString()[i]);
                System.out.println(clientTo);
                if (clientTo != null) {
                    try {
                        Util.sendMessage(usersFromDBMessage(), clientTo.outputStream);
                    } catch (IOException e) {
                        System.out.println("Cannot send the list of registered users to" + clientTo + " client");
                    }
                } else {
                    System.out.println("User not present");
                }
            }
        }
    }

    public static void createUserTable(Connection connection, String user) {
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "
                + user + " (content text, sourceUsername text, toWho text)")) {
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void putTextInDB(Connection connection, Message message) {
        String tableName = message.getSourceUsername();
        String content = message.getContent();
        String toWho = message.getToWho();
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (content, sourceUsername, toWho)" +
                " VALUES ('" + content + "','" + tableName + "','" + toWho + "')");
                PreparedStatement statement2 = connection.prepareStatement("INSERT INTO " + toWho + " (content, sourceUsername, toWho)" +
                        " VALUES ('" + content + "','" + tableName + "','" + toWho + "')")) {
            statement.execute();
            statement2.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void sendHistory(Connection connection, Message message)
    {
        try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + message.getSourceUsername() + ";")) {
            ResultSet resultSet = statement.executeQuery();
            String[] content = new String[5];
            String[] sourceUsername= new String[5];
            String[] toWho= new String[5];
            int i=-1;
            while (resultSet.next())
            {
                i++;
                if (i == content.length)
                {
                    content = Arrays.copyOf(content,i*2);
                    sourceUsername = Arrays.copyOf(sourceUsername,i*2);
                    toWho = Arrays.copyOf(toWho,i*2);
                }
                content[i]= resultSet.getString(1);
                sourceUsername[i] = resultSet.getString(2);
                toWho[i]= resultSet.getString(3);
            }
            Message message1 = new Message(MessageType.SENDHISTORY, content, sourceUsername,toWho);
            Util.sendMessage(message1,this.outputStream);
        }
        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }
    }
}