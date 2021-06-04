package Client.JavaFX;

import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Common.Util;
import Client.Client;
import java.io.IOException;
import Client.ReaderThread;

public class LoginController {

    /**
     * A belépésért felelős controller osztály. Főbb feladata, hogy ha ReaderThread-ben a loginAccepted változó
     * true értéket vesz fel, azaz a szerver az adatbázisból leellenőrizte, hogy a felhasználó a megfelelő jelszót
     * vitte-e be a felületre, akkor továbbengedi a user-t a MainPage felületére, ahol el is indítja a user nevének a
     * kiírásáért felelős metódust, valamint ha a regisztrációra nyomott rá a user, akkor ennek megfelelően a
     * Registration felületét hozzá fel
     */

    @FXML
    TextField userNameField;
    @FXML
    TextField passwordField;

    public static String username;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void login(ActionEvent event) throws IOException, InterruptedException {
        username = userNameField.getText();
        String password = passwordField.getText();
        Message message = Message.loginAs(username,password);
        Util.sendMessage(message, Client.outputStream);
        /*
          a sleep() azért szükséges, hogy a szál megvárja, hogy a szerver elküldje neki a user-eket
         */
        Thread.sleep(100);

        if (ReaderThread.loginAccepted)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("XMLs/MainPage.fxml"));
            root = loader.load();
            MainController controller2 = loader.getController();
            controller2.displayName(username);
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void register(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("XMLs/RegisterPage.fxml"));
        root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
