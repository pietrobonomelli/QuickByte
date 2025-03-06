package utilities;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LogoUtilities {

    public static ImageView createLogo() {  // Metodo statico
        // Usa la classe Image di JavaFX
        InputStream logoStream = LogoUtilities.class.getResourceAsStream("/images/LogoQuickByte.png");

        if (logoStream == null) {
            System.out.println("Errore: immagine del logo non trovata.");
        } else {
            System.out.println("Logo caricato con successo!");
        }

        // Creazione corretta dell'oggetto Image di JavaFX
        Image logoImage = new Image(logoStream);
        // Creazione di ImageView con l'immagine
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);
        return logoView;
    }
}
