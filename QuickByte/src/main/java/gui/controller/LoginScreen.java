package gui.controller;

import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends VBox {

    public LoginScreen() {
        // Imposta la classe CSS per il layout principale
        this.getStyleClass().add("login-container");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);

        // Aggiungi il logo
        ImageView logoView = createLogo();

        // Titolo con font maggiore
        Text title = new Text("Benvenuto su QuickByte");
        title.getStyleClass().add("title");

        // Crea i campi di input per email e password
        TextField emailField = new TextField();
        emailField.setPromptText("Inserisci l'email");
        emailField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci la password");
        passwordField.getStyleClass().add("password-field");

        // Crea il pulsante di login
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        // Crea il pulsante per la registrazione
        Button registerButton = new Button("Registrati");
        registerButton.getStyleClass().add("button-secondary");

        // Gestisci il click del pulsante di login
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (verifyUser(email, password)) {
                // Salva l'email nella sessione
                SessioneUtente.setEmail(email);
                showAlert(AlertType.INFORMATION, "Login riuscito", "Benvenuto, " + email);
                switchToMainScreen(email);
            } else {
                showAlert(AlertType.ERROR, "Login fallito", "Email o password errati.");
            }
        });

        // Gestisci il click del pulsante di registrazione
        registerButton.setOnAction(e -> {
            // Creiamo un nuovo layout per la schermata di registrazione
            RegisterScreen registrationScreen = new RegisterScreen();

            // Otteniamo la finestra attuale
            Stage primaryStage = (Stage) registerButton.getScene().getWindow();

            // Creiamo una nuova scena con la stessa dimensione della finestra attuale
            Scene registrationScene = new Scene(registrationScreen, primaryStage.getWidth(), primaryStage.getHeight());
            registrationScene.getStylesheets().add("style.css");

            // Impostiamo la nuova scena senza cambiare le dimensioni della finestra
            primaryStage.setScene(registrationScene);
        });

        // Aggiungi gli elementi al layout
        this.getChildren().addAll(logoView, title, emailField, passwordField, loginButton, registerButton);
    }

    private void switchToMainScreen(String email) {
        String tipoUtente = getUserType(email);

        if (tipoUtente == null) {
            showAlert(AlertType.ERROR, "Errore", "Impossibile determinare il tipo di utente.");
            return;
        }

        Stage primaryStage = (Stage) getScene().getWindow();
        Scene newScene;

        // Verifica che il tipo utente esista prima di continuare
        if (tipoUtente != null) {
            switch (tipoUtente) {
                case "Cliente":
                    newScene = new Scene(new MainScreenCliente(), primaryStage.getWidth(), primaryStage.getHeight());
                    break;
                case "Corriere":
                    newScene = new Scene(new MainScreenCorriere(), primaryStage.getWidth(), primaryStage.getHeight());
                    break;
                case "Titolare":
                    newScene = new Scene(new MainScreenTitolare(), primaryStage.getWidth(), primaryStage.getHeight());
                    break;
                default:
                    showAlert(AlertType.ERROR, "Errore", "Tipo utente non riconosciuto.");
                    return;
            }

            newScene.getStylesheets().add("style.css");
            primaryStage.setScene(newScene);
        } else {
            showAlert(AlertType.ERROR, "Errore", "Tipo utente non valido.");
        }
    }


    // Metodo per creare il logo
    private ImageView createLogo() {
        InputStream logoStream = getClass().getResourceAsStream("/images/LogoQuickByte.png");
        if (logoStream == null) {
            System.out.println("Errore: immagine del logo non trovata.");
        } else {
            System.out.println("Logo caricato con successo!");
        }

        Image logoImage = new Image(logoStream);
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);
        return logoView;
    }

    // Metodo per verificare l'utente nel database
    public boolean verifyUser(String email, String password) {
        String query = "SELECT * FROM Utente WHERE email = ? AND password = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Errore durante la verifica dell'utente: " + e.getMessage());
            return false;
        }
    }

    // Metodo per mostrare un messaggio di alert
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String getUserType(String email) {
        String query = "SELECT tipoUtente FROM Utente WHERE email = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("tipoUtente");
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il recupero del tipo di utente: " + e.getMessage());
        }
        return null;
    }
}
