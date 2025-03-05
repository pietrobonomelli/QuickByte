package gui.main;

import dao.LoginDAO;
import gui.cliente.MainScreenCliente;
import gui.corriere.MainScreenCorriere;
import gui.titolare.MainScreenTitolare;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sessione.SessioneUtente;

import java.io.InputStream;

public class LoginScreen extends VBox {

    public LoginScreen() {
        this.getStyleClass().add("login-container");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);

        ImageView logoView = createLogo();
        Text title = new Text("Benvenuto su QuickByte");
        title.getStyleClass().add("title");

        TextField emailField = new TextField();
        emailField.setPromptText("Inserisci l'email");
        emailField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci la password");
        passwordField.getStyleClass().add("password-field");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        Button registerButton = new Button("Registrati");
        registerButton.getStyleClass().add("button-secondary");

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (LoginDAO.getInstance().verifyUser(email, password)) {
                SessioneUtente.setEmail(email);
                Utilities.showAlert("Login riuscito", "Benvenuto, " + email);
                switchToMainScreen(email);
            } else {
            	Utilities.showAlert("Login fallito", "Email o password errati.");
            }
        });

        registerButton.setOnAction(e -> {
            RegisterScreen registrationScreen = new RegisterScreen();
            Stage primaryStage = (Stage) registerButton.getScene().getWindow();
            Scene registrationScene = new Scene(registrationScreen, primaryStage.getWidth(), primaryStage.getHeight());
            registrationScene.getStylesheets().add("style.css");
            primaryStage.setScene(registrationScene);
        });

        this.getChildren().addAll(logoView, title, emailField, passwordField, loginButton, registerButton);
    }

    private void switchToMainScreen(String email) {
        String tipoUtente = LoginDAO.getInstance().getUserType(email);

        if (tipoUtente == null) {
        	Utilities.showAlert("Errore", "Impossibile determinare il tipo di utente.");
            return;
        }

        Stage primaryStage = (Stage) getScene().getWindow();
        Scene newScene;

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
            	Utilities.showAlert("Errore", "Tipo utente non riconosciuto.");
                return;
        }

        newScene.getStylesheets().add("style.css");
        primaryStage.setScene(newScene);
    }

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
}
