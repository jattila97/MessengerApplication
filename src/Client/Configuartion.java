package Client;

import java.io.*;
import java.util.Properties;

/**
 * A properties megvalósításáért felelős osztály. Eltárolja a user által a bezárás során beállított ablak-
 * pozíciót. A Main-ben hívódik meg. A config.properties fájlba menti ki a pozíciót és a Main onnan tölti be
 * az itteni readFromOutput függvény segítségével.
 */

public class Configuartion {
    private final Properties properties = new Properties();
    public static String POSITIONX =  "positionX";
    public static String POSITIONY = "positionY";
    private final String FILENAME = "src/Client/config.properties";

    public Configuartion()
    {

    }

    private void writeToOutput()
    {
        try
        {
            this.properties.store(new FileOutputStream(FILENAME), null);
        } catch (IOException e) {
            System.out.println("Cannot write properties to file!");
        }
    }

    public void readFromOutput()
    {
        try
        {
            this.properties.load(new FileInputStream(FILENAME));
            System.out.println(this.properties.getProperty(POSITIONX));
        } catch (IOException e) {
            System.out.println("Cannot read properties from file!");
        }
    }

    public Properties getProperties() {
        return properties;
    }
    public void setPos(double px, double py)
    {
        this.properties.setProperty(POSITIONX,String.valueOf(px));
        this.properties.setProperty(POSITIONY,String.valueOf(py));
        this.writeToOutput();
    }
}
