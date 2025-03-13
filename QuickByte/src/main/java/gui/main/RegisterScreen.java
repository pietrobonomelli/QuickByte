package gui.main;

import java.sql.SQLException;
import dao.UtenteDAO;
import database.PopolaDatabase;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.*;
import utilities.*;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe che rappresenta la schermata di registrazione.
 */
public class RegisterScreen extends StackPane {

    private TextField emailField;
    private TextField nameField;
    private TextField phoneField;
    private PasswordField passwordField;
    private ComboBox<String> userTypeComboBox;
    private Button registerButton;

    public RegisterScreen() {
        this.getStyleClass().add("login-container");
        setAlignment(Pos.CENTER);

        ImageView logoView = LogoUtilities.createLogo();
        Text title = new Text("Benvenuto su QuickByte - Il gusto a portata di click!");
        title.getStyleClass().add("title");

        String presentazioneRuoli =
                "- Cliente: Esplora i ristoranti, sfoglia i menu, effettua ordini e segui il loro stato.\n" +
                "- Titolare: Gestisci il tuo menu e organizza gli ordini dei clienti.\n" +
                "- Corriere: Visualizza le consegne disponibili e aggiorna il loro stato.";

        Text presentazione = new Text(presentazioneRuoli);
        presentazione.getStyleClass().add("role-list");

        VBox registrazioneForm = createRegistrationForm();

        VBox loginButtonBox = createLoginButtonBox();

        Button popolaDB = new Button("Popola il database");
        popolaDB.setOnAction(e -> PopolaDatabase.popolaDatabase());

        VBox content = new VBox(logoView, title, presentazione, registrazioneForm, loginButtonBox, popolaDB);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        getChildren().add(scrollPane);
    }

    /**
     * Crea il modulo di registrazione.
     *
     * @return Il VBox contenente il modulo di registrazione.
     */
    private VBox createRegistrationForm() {
        VBox registrazioneForm = new VBox();
        registrazioneForm.setAlignment(Pos.CENTER);
        registrazioneForm.setSpacing(5);

        Label titleRegistrazione = new Label("REGISTRAZIONE");
        titleRegistrazione.getStyleClass().add("title");

        emailField = new TextField();
        nameField = new TextField();
        phoneField = new TextField();
        passwordField = new PasswordField();

        VBox emailBox = Utilities.createFieldBox("E-MAIL", "Inserisci l'e-mail", emailField);
        VBox nomeBox = Utilities.createFieldBox("NOME", "Inserisci il nominativo", nameField);
        VBox telefonoBox = Utilities.createFieldBox("NUMERO DI TELEFONO", "Inserisci il numero di telefono", phoneField);
        VBox passwordBox = Utilities.createFieldBox("PASSWORD", "Inserisci la password", passwordField);

        userTypeComboBox = new ComboBox<>();
        userTypeComboBox.getItems().addAll("Cliente", "Titolare", "Corriere");
        userTypeComboBox.setPromptText("Seleziona il tipo di utente");

        registerButton = new Button("Registrati");
        registerButton.setOnAction(e -> {
            try {
                handleRegistration();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        registrazioneForm.getChildren().addAll(titleRegistrazione, emailBox, nomeBox, telefonoBox, passwordBox, userTypeComboBox, registerButton);
        return registrazioneForm;
    }

    /**
     * Crea la sezione del pulsante di login.
     *
     * @return Il VBox contenente il pulsante di login.
     */
    private VBox createLoginButtonBox() {
        VBox loginButtonBox = new VBox();
        Text loginLabel = new Text("Se hai già un account: ");
        loginLabel.getStyleClass().add("label");
        loginButtonBox.setAlignment(Pos.CENTER);
        loginButtonBox.setSpacing(5);
        loginButtonBox.setMaxWidth(280);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button-secondary");
        loginButton.setOnAction(e -> switchToLoginScreen());

        loginButtonBox.getChildren().addAll(loginLabel, loginButton);
        return loginButtonBox;
    }

    /**
     * Gestisce il processo di registrazione.
     *
     * @throws SQLException Se si verifica un errore SQL durante la registrazione.
     */
    private void handleRegistration() throws SQLException {
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
        if (UtenteDAO.getInstance().getUtenteByEmail(email) != null) {
            showError("Email già in uso.");
        } else {
        	//hasha la password
        	String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        	
        	// Crea l'oggetto utente e lo inserisce nel database
            Utente utente = null;
            switch (userType) {
                case "Cliente":
                    utente = new Cliente(email, hashedPassword, name, phone);
                    break;
                case "Titolare":
                    utente = new Titolare(email, hashedPassword, name, phone);
                    break;
                case "Corriere":
                    utente = new Corriere(email, hashedPassword, name, phone);
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

    /**
     * Passa alla schermata di login.
     */
    private void switchToLoginScreen() {
        Scene currentScene = getScene();
        LoginScreen loginScreen = new LoginScreen();
        currentScene.setRoot(loginScreen);
    }

    /**
     * Mostra un messaggio di errore.
     *
     * @param message Il messaggio di errore da visualizzare.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra un messaggio di successo.
     *
     * @param message Il messaggio di successo da visualizzare.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
