package gui.main;

import dao.LoginDAO;
import gui.cliente.MainScreenCliente;
import gui.corriere.MainScreenCorriere;
import gui.titolare.MainScreenTitolare;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import sessione.SessioneUtente;
import utilities.*;

public class LoginScreen extends VBox {

    public LoginScreen() {
        this.getStyleClass().add("login-container");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        ImageView logoView = LogoUtilities.createLogo();
        Text title = new Text("Bentornato su QuickByte - Il gusto a portata di click!");
        title.getStyleClass().add("title");

        Label titleLogin = Utilities.createLabel("LOGIN", "title");

        VBox emailBox = Utilities.createFieldBox("E-MAIL", "Inserisci l'e-mail", new TextField());
        VBox passwordBox = Utilities.createFieldBox("PASSWORD", "Inserisci la password", new PasswordField());

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(emailBox, passwordBox));

        VBox registerButtonBox = createRegisterButtonBox();

        VBox content = new VBox(logoView, title, titleLogin, emailBox, passwordBox, loginButton, registerButtonBox);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        this.getChildren().add(scrollPane);
    }

    /**
     * Gestisce il login.
     *
     * @param emailBox    Il VBox contenente il campo email.
     * @param passwordBox Il VBox contenente il campo password.
     */
    private void handleLogin(VBox emailBox, VBox passwordBox) {
        TextField emailField = (TextField) emailBox.getChildren().get(1);
        PasswordField passwordField = (PasswordField) passwordBox.getChildren().get(1);

        String email = emailField.getText();
        String password = passwordField.getText();

        if (LoginDAO.getInstance().verifyUser(email, password)) {
            SessioneUtente.setEmail(email);
            Utilities.showAlert("Login riuscito", "Benvenuto, " + email);
            switchToMainScreen(email);
        } else {
            Utilities.showAlert("Login fallito", "Email o password errati.");
        }
    }

    /**
     * Crea la sezione del pulsante di registrazione.
     *
     * @return Il VBox contenente il pulsante di registrazione.
     */
    private VBox createRegisterButtonBox() {
        VBox registerButtonBox = new VBox();
        Text registrazioneLabel = new Text("Se non hai un account: ");
        registrazioneLabel.getStyleClass().add("label");
        registerButtonBox.setAlignment(Pos.CENTER);
        registerButtonBox.setSpacing(5);
        registerButtonBox.setMaxWidth(280);

        Button registerButton = new Button("Registrati");
        registerButton.getStyleClass().add("button-secondary");
        registerButton.setOnAction(e -> switchToRegisterScreen());

        registerButtonBox.getChildren().addAll(registrazioneLabel, registerButton);
        return registerButtonBox;
    }

    /**
     * Passa alla schermata di registrazione.
     */
    private void switchToRegisterScreen() {
        RegisterScreen registrationScreen = new RegisterScreen();
        Stage primaryStage = (Stage) this.getScene().getWindow();
        Scene registrationScene = new Scene(registrationScreen, primaryStage.getWidth(), primaryStage.getHeight());
        registrationScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        primaryStage.setScene(registrationScene);
    }
    
    /**
     * Passa alla schermata principale in base al tipo di utente.
     *
     * @param email L'email dell'utente loggato.
     */
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

        newScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());
        primaryStage.setScene(newScene);
    }
}
