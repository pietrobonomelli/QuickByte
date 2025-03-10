package utilities;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class LogoUtilities {

	private static final int WIDTH_LOGO = 200;
	
    public static ImageView createLogo() {  
        InputStream logoStream = LogoUtilities.class.getResourceAsStream("/images/LogoQuickByte.png");

        if (logoStream == null) {
            System.out.println("Errore: immagine del logo non trovata.");
            return null;
        }

        Image logoImage = new Image(logoStream);
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(WIDTH_LOGO);
        logoView.setPreserveRatio(true);

        // Creazione di una clip circolare
        double radius = WIDTH_LOGO/2;
        Circle clip = new Circle(radius);
        clip.setCenterX(radius);
        clip.setCenterY(radius);

        // Applica la clip all'ImageView
        logoView.setClip(clip);

        return logoView;
    }
}
