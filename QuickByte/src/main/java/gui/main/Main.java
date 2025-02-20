package gui.main;

import database.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        System.out.println("Avvio applicazione...");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Avvio interfaccia grafica...");
        
        // Crea le tabelle del database (se non esistono già)
        DatabaseManager.createTables();

        // Imposta il titolo della finestra
        primaryStage.setTitle("Food Delivery - QuickByte");

        // Crea la schermata di login (LoginScreen)
        LoginScreen loginScreen = new LoginScreen();

        // Imposta la scena con la LoginScreen
        Scene scene = new Scene(loginScreen, 800, 600);  // 400x300 è la dimensione della finestra
        
        // Carica il CSS
        URL cssUrl = getClass().getResource("/gui/styles/style.css");
        if (cssUrl == null) {
            System.out.println("Errore: file CSS non trovato.");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        
        primaryStage.setScene(scene);

        // Mostra la finestra
        primaryStage.show();

        System.out.println("Interfaccia grafica avviata correttamente!");
    }
}
