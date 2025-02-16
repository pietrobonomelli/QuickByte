package gui.controller;

import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterScreen extends VBox {

    private TextField emailField;
    private TextField nameField;
    private TextField phoneField;
    private PasswordField passwordField;
    private ComboBox<String> userTypeComboBox;
    private Button registerButton;

    public RegisterScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-background-color: orange;");

        Label titleLabel = new Label("Registrazione");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        
        nameField = new TextField();
        nameField.setPromptText("Nome");
        
        phoneField = new TextField();
        phoneField.setPromptText("Numero di telefono");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("Cliente", "Titolare", "Corriere");
        userTypeComboBox.setPromptText("Seleziona tipo utente");
        
        registerButton = new Button("Registrati");
        registerButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        registerButton.setOnAction(e -> handleRegistration());
        
        Button tornaButton = new Button("Torna al Login");
        tornaButton.setOnAction(e -> switchToLoginScreen());

        getChildren().addAll(titleLabel, emailField, nameField, phoneField, passwordField, userTypeComboBox, registerButton, tornaButton);
    }

    private void handleRegistration() {
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();
        String userType = userTypeComboBox.getValue();

        if (email.isEmpty() || name.isEmpty() || phone.isEmpty() || password.isEmpty() || userType == null) {
            showError("Tutti i campi sono obbligatori.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Formato email non valido.");
            return;
        }

        if (!phone.matches("\\d{10,15}")) {
            showError("Numero di telefono non valido.");
            return;
        }

        if (isEmailTaken(email)) {
            showError("Email già in uso.");
        } else {
            saveUser(email, name, phone, password, userType);
            showSuccess("Registrazione avvenuta con successo!");
            switchToLoginScreen();
        }
    }
    
    //QUESTO RIPORTA AL LOGIN
    private void switchToLoginScreen() {
        // Ottieni il contesto della scena corrente
        Scene currentScene = getScene();

        // Crea la nuova schermata di login
        LoginScreen loginScreen = new LoginScreen(); 

        // Cambia la scena con quella di login
        currentScene.setRoot(loginScreen);
    }

    private boolean isEmailTaken(String email) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM Utente WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveUser(String email, String name, String phone, String password, String userType) {
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO Utente (email, nome, telefono, password, tipoUtente) VALUES (?, ?, ?, ?, ?)")
        ) {
            statement.setString(1, email);
            statement.setString(2, name);
            statement.setString(3, phone);
            statement.setString(4, password);
            statement.setString(5, userType);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Errore durante la registrazione. Riprova.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
