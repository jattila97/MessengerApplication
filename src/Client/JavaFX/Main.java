package Client.JavaFX;

import Client.Client;
import Client.Configuartion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static double px;
    private static double py;
    public static Stage primaryStage1;

    /**
     * A kliens felületének az elindításáért felelős osztály. Beállítja az alkalmazás tálcán megjelenő ikonját,
     * és a nevét is. Továbbá előírja, hogy a loginPage.fxml oldal megnyitásával induljon az alkalmazás.
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("XMLs/LoginPage.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Chat Application");
        Image icon = new Image("Client/JavaFX/images/chaticon2.jpg");
        primaryStage.getIcons().add(icon);
        if (px!=0 && py !=0)
        {
            primaryStage.setX(px);
            primaryStage.setY(py);
        }

        primaryStage.show();
        primaryStage1 = primaryStage;
    }

    /**
     * Az indulás előtt betölti a config.properties fájlból az ablak legutóbbi helyzetét, valamint leállás
     * után el is menti azt.
     */
    public static void main(String[] args) throws IOException {
        Configuartion myConfiguration = new Configuartion();
        myConfiguration.readFromOutput();
        if (myConfiguration.getProperties().get(Configuartion.POSITIONX) != null)
        {
            px = Double.parseDouble(String.valueOf(myConfiguration.getProperties().get(Configuartion.POSITIONX)));
            py = Double.parseDouble(String.valueOf(myConfiguration.getProperties().get(Configuartion.POSITIONY)));
        }
        new Client();
        launch(args);
        myConfiguration.setPos(Main.primaryStage1.getX(),Main.primaryStage1.getY());
    }
}