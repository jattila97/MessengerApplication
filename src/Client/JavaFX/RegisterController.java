package Client.JavaFX;

import Client.Client;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import static Common.Util.sendMessage;

public class RegisterController {
    @FXML
    TextField regEmail = new TextField();
    @FXML
    TextField regUserName = new TextField();
    @FXML
    TextField regPassword = new TextField();

    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * A regisztráció felületének a controller-je. Egy REGISTER típusú üzenetet küld el a szervernek
     */
    public void register(ActionEvent event) throws IOException {
        String email = regEmail.getText();
        String username = regUserName.getText();
        String password = regPassword.getText();
        String messageContent = email+";"+username+";"+password;
        Message message = Message.registerAs(username,messageContent);
        sendMessage(message, Client.outputStream);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("XMLs/LoginPage.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("XMLs/LoginPage.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
