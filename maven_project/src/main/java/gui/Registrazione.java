package gui;

import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registrazione extends VBox {

    private TextField emailField;
    private TextField nameField;
    private TextField phoneField;
    private PasswordField passwordField;
    private Button registerButton;

    public Registrazione() {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setStyle("-fx-background-color: orange;");

        Label titleLabel = new Label("Registrazione");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        nameField = new TextField();
        nameField.setPromptText("Nome");
        nameField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        phoneField = new TextField();
        phoneField.setPromptText("Numero di telefono");
        phoneField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        registerButton = new Button("Registrati");
        registerButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 10;");
        registerButton.setOnAction(e -> handleRegistration());

        getChildren().addAll(titleLabel, emailField, nameField, phoneField, passwordField, registerButton);
    }

    private void handleRegistration() {
        String email = emailField.getText();
        String name = nameField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (isEmailTaken(email)) {
            showError("Email già in uso");
        } else {
            saveUser(email, name, phone, password);
            showSuccess("Registrazione avvenuta con successo!");
        }
    }

    private boolean isEmailTaken(String email) {
        try (Connection connection = DatabaseConnection.connect()) {
            String query = "SELECT * FROM Utente WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveUser(String email, String name, String phone, String password) {
        try (Connection connection = DatabaseConnection.connect()) {
            String query = "INSERT INTO Utente (email, nome, telefono, password) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, name);
            statement.setString(3, phone);
            statement.setString(4, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
