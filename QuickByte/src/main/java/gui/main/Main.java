package gui.main;

import database.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Classe principale dell'applicazione che estende Application di JavaFX.
 */
public class Main extends Application {

    private final int MIN_WIDTH = 900;
    private final int MIN_HEIGHT = 800;

    /**
     * Metodo principale per avviare l'applicazione.
     *
     * @param args Argomenti della riga di comando.
     */
    public static void main(String[] args) {
        System.out.println("Avvio applicazione...");
        launch(args);
    }

    /**
     * Metodo chiamato per inizializzare l'interfaccia grafica.
     *
     * @param primaryStage Lo stage principale dell'applicazione.
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Avvio interfaccia grafica...");

        // Crea le tabelle del database se non esistono già
        DatabaseManager.createTables();

        // Imposta il titolo della finestra
        primaryStage.setTitle("Food Delivery - QuickByte");

        // Crea la schermata di login
        LoginScreen loginScreen = new LoginScreen();

        // Crea la scena per il login
        Scene scene = new Scene(loginScreen, MIN_WIDTH, MIN_HEIGHT);

        // Carica il file CSS per la scena
        caricaCSS(scene);

        // Imposta la scena principale e mostra la finestra
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Interfaccia grafica avviata correttamente!");
    }

    /**
     * Carica il file CSS per la scena.
     *
     * @param scene La scena a cui applicare il CSS.
     */
    private void caricaCSS(Scene scene) {
        URL cssUrl = getClass().getResource("/style/style.css");
        if (cssUrl == null) {
            System.err.println("Errore: file CSS non trovato. Verifica il percorso del file.");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("CSS caricato correttamente!");
        }
    }
}
