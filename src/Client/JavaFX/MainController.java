package Client.JavaFX;

import Client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static Common.MessageType.TEXT;
import static Common.Util.sendMessage;
import Common.Message;
import Client.ReaderThread;

/**
 * A fő oldal megjelenítésére való osztály. A toWho paraméter a ListView-ban kijelölt user String értékét veszi
 * fel és ez majd az elküldött message toWho paramétereként fog szolgálni.
 */
public class MainController implements Initializable {
    private static String toWho="";
    @FXML
    ListView<String> Listview1 = new ListView<String>();
    @FXML
    Label nameLabel;
    @FXML
    TextField messageTextField;
    @FXML
    public Button sendButton;
    @FXML
    ListView messagesListView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Listview1.getItems().addAll(ReaderThread.getUsers());

        Listview1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                refreshMessages();
            }
        });
    }

    public void displayName(String username)
    {
        nameLabel.setText(username + "'s account");
    }
    public void sendMessageMethod() throws IOException {
        String messageContent = messageTextField.getText();
        Message message = new Message(TEXT, LoginController.username,messageContent,toWho);
        sendMessage(message,Client.outputStream);
        String message1 = message.getSourceUsername() + ": " + message.getContent();
        ReaderThread.messList.add(message1);
        ReaderThread.messFromTo.add(message.getSourceUsername() + "-" + message.getToWho());
        refreshMessages();
    }
    public void logout(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout");



        if (alert.showAndWait().get() == ButtonType.OK)
        {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.close();
            Message message1 = Message.logout();
            sendMessage(message1,Client.outputStream);
        }
    }
    public void refreshListview()
    {

        /**
         * A userek frissítésért felelős függvény. Alapvetően a readerThread egyből veszi, ha egy új user regisztrál,
         * viszont a felületen való megjelenítéshez egy event-et kell generálni, hogy változzon a listView1
         */

        for (int i = 0; i<ReaderThread.getUsers().length; i++)
        {
            Listview1.getItems().remove(ReaderThread.getUsers()[i]);
        }
        Listview1.getItems().addAll(ReaderThread.getUsers());
    }
    private void displayMessage(String partner)
    {
        String user =ReaderThread.getUserName();
        int size = ReaderThread.messList.size();
        for (int i = 0; i< size; i++)
        {
            String[] strings = ReaderThread.messFromTo.get(i).split("-");
            String sender = strings[0];
            String receiver = strings[1];
            if (sender.equals(user))
            {
                if (receiver.equals(partner))
                {
                    messagesListView.getItems().addAll(ReaderThread.messList.get(i));
                }
            }
            else if (sender.equals(partner))
            {
                if (receiver.equals(user))
                {
                    messagesListView.getItems().addAll(ReaderThread.messList.get(i));
                }
            }
        }
    }
    public void refreshMessages()
    {
        toWho = Listview1.getSelectionModel().getSelectedItem();
        if (ReaderThread.messList!=null)
        {
            for(int i = 0; i<ReaderThread.messList.size();i++)
            {
                messagesListView.getItems().remove(ReaderThread.messList.get(i));
            }
        }
        displayMessage(toWho);
    }
}
