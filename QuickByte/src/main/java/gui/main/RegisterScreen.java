package gui.main;

import dao.UtenteDAO;
import database.PopolaDatabase;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.*;

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

        Button popolaDB = new Button("Popola il database");
        popolaDB.setOnAction(e -> PopolaDatabase.popolaDatabase());

        getChildren().addAll(titleLabel, emailField, nameField, phoneField, passwordField, userTypeComboBox, registerButton, tornaButton, popolaDB);
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

        // Controlla se l'email è già in uso
        System.out.println("return del get utente by email: " + UtenteDAO.getInstance().getUtenteByEmail(email));
        if (UtenteDAO.getInstance().getUtenteByEmail(email) != null) {
            showError("Email già in uso.");
        } else {
            // Crea l'oggetto utente e lo inserisce nel database
            Utente utente = null;
            switch (userType) {
                case "Cliente":
                    utente = new Cliente(email, password, name, phone);
                    break;
                case "Titolare":
                    utente = new Titolare(email, password, name, phone);
                    break;
                case "Corriere":
                    utente = new Corriere(email, password, name, phone);
                    break;
            }

            // Inserisce l'utente nel database tramite il DAO
            if (utente != null) {
                boolean success = UtenteDAO.getInstance().insertUtente(utente, userType);
                if (success) {
                    showSuccess("Registrazione avvenuta con successo!");
                    switchToLoginScreen();
                } else {
                    showError("Errore durante la registrazione. Riprova.");
                }
            }
        }
    }
    
    private void switchToLoginScreen() {
        Scene currentScene = getScene();
        LoginScreen loginScreen = new LoginScreen(); 
        currentScene.setRoot(loginScreen);
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
