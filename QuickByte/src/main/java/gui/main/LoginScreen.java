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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sessione.*;
import utilities.LogoUtilities;
import utilities.Utilities;

public class LoginScreen extends VBox {

    public LoginScreen() {
        this.getStyleClass().add("login-container");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);

        ImageView logoView = LogoUtilities.createLogo();
        Text title = new Text("Bentornato su QuickByte - Il gusto a portata di click!");
        title.getStyleClass().add("title");

        Text titleLogin = new Text("Effettua il login");
        titleLogin.getStyleClass().add("title");
        
        VBox emailBox = new VBox();
	        Text emailLabel = new Text("E-MAIL");
	        emailLabel.getStyleClass().add("label");
	        TextField emailField = new TextField();
	        emailField.setPromptText("Inserisci l'e-mail");
	        emailField.getStyleClass().add("text-field");
	        emailField.setMaxWidth(280);
	    emailBox.getChildren().addAll(emailLabel, emailField);
        emailBox.getStyleClass().add("field-box");

        VBox passwordBox = new VBox();
	        Text passwordLabel = new Text("PASSWORD");
	        passwordLabel.getStyleClass().add("label");
	        PasswordField passwordField = new PasswordField();
	        passwordField.setPromptText("Inserisci la password");
	        passwordField.getStyleClass().add("password-field");
	        passwordField.setMaxWidth(280);
	    passwordBox.getChildren().addAll(passwordLabel, passwordField);
	    passwordBox.getStyleClass().add("field-box");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");

        VBox registerButtonBox = new VBox();
        Text registrazioneLabel = new Text("Se non hai un account: ");
        registrazioneLabel.getStyleClass().add("label");
        // Usa VBox per allineare il testo e il bottone a sinistra
        registerButtonBox.setAlignment(Pos.CENTER);
        registerButtonBox.setSpacing(5);
        registerButtonBox.setMaxWidth(280);
        Button registerButton = new Button("Registrati");
        registerButton.getStyleClass().add("button-secondary");
        registerButton.setAlignment(Pos.CENTER); // Allinea il bottone a sinistra

        registerButtonBox.getChildren().addAll(registrazioneLabel, registerButton);

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
            registrationScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
            System.out.println(getClass().getResource("/style/style.css"));

            primaryStage.setScene(registrationScene);
        });

        this.getChildren().addAll(logoView, title, titleLogin, emailBox, passwordBox, loginButton, registerButtonBox);

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

}