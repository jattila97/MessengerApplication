package Common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Client.JavaFX.LoginController;
import static Common.MessageType.SENDUSERS;
import java.io.IOException;
import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String sourceUsername;
    private String content;
    public String toWho;
    private String[] contentArray;
    private String[] sourceUsernameArray;
    private String[] toWhoArray;

    public Message() {
    }

    public Message(MessageType type, String sourceUsername, String content, String To) {
        this.type = type;
        this.sourceUsername = sourceUsername;
        this.content = content;
        this.toWho=To;
    }

    public Message(MessageType type, String[] content, String[] sourceUsernameArray, String[] toWhoArray)
    {
        this.type = type;
        this.contentArray = content;
        this.sourceUsernameArray = sourceUsernameArray;
        this.toWhoArray = toWhoArray;
    }

    public static Message parse(byte[] bytes) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(bytes, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Message loginAs(String username, String password) {
        return new Message(MessageType.LOGIN, username,password, "");
    }
    public static Message registerAs(String username, String password) {
        return new Message(MessageType.REGISTER, username,password, "");
    }
    public static Message accepted(String user)
    {
        return new Message(MessageType.ACCEPTED, "",user,"");
    }
    public static Message errorUser()
    {
        return new Message(MessageType.ERRORUSER, "","","");
    }
    public static Message errorPass()
    {
        return new Message(MessageType.ERRORPASS, "","","");
    }
    public static Message sendUsers(String[] users)
    {
        String usersToString = "";
        for (int i=0; i<users.length; i++)
        {
            if (users[i]!=null && i>0)
            {
                usersToString = usersToString + ";" + users[i];
            }
            else if (users[i]!=null)
            {
                usersToString = users[i];
            }
        }
        return new Message(SENDUSERS, "",usersToString, "");
    }
    public static Message logout()
    {
        return new Message(MessageType.LOGOUT, LoginController.username,"","");
    }

    public byte[] toBytes() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getToWho() {
        return toWho;
    }

    public String[] getContentArray() {
        return contentArray;
    }

    public void setContentArray(String[] contentArray) {
        this.contentArray = contentArray;
    }

    public String[] getSourceUsernameArray() {
        return sourceUsernameArray;
    }

    public void setSourceUsernameArray(String[] sourceUsernameArray) {
        this.sourceUsernameArray = sourceUsernameArray;
    }

    public String[] getToWhoArray() {
        return toWhoArray;
    }

    public void setToWhoArray(String[] toWhoArray) {
        this.toWhoArray = toWhoArray;
    }
}
