package quickbyte;

import database.DatabaseManager;
import gui.LoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        Scene scene = new Scene(loginScreen, 400, 300);  // 400x300 è la dimensione della finestra
        
        // Carica il CSS
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);

        // Mostra la finestra
        primaryStage.show();

        System.out.println("Interfaccia grafica avviata correttamente!");
    }
}
