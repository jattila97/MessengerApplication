package Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Az üzenetek byte tömbbé alakításáért felelős osztály, mindez pedig a Message osztályban található toBytes() és
 * parse() metódusokkal történik, amik az objectMapper segítségével alakítják a Message objektumokat át byte tömbbé
 * valamint fordítva.
 */
public class Util {
    public static void sendMessage(Message message, DataOutputStream outputStream) throws IOException {
        byte[] msgBytes = message.toBytes();
        outputStream.writeInt(msgBytes.length);
        outputStream.write(msgBytes);
    }

    public static Message readMessage(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();
        if(length>0) {
            byte[] bytes = new byte[length];
            inputStream.readFully(bytes, 0, bytes.length);
            return Message.parse(bytes);
        } else {
            return null;
        }
    }
}
