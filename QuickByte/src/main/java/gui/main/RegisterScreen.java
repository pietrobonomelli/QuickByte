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
import utilities.LogoUtilities;

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

        ImageView logoView = LogoUtilities.createLogo();
        Text title = new Text("Benvenuto su QuickByte - Il gusto a portata di click!");
        title.getStyleClass().add("title");
		
		Label titleRegistrazione = new Label("Registrazione");
		titleRegistrazione.getStyleClass().add("title");

		VBox emailBox = new VBox();
		Text emailLabel = new Text("E-MAIL");
		emailLabel.getStyleClass().add("label");
		TextField emailField = new TextField();
		emailField.setPromptText("Inserisci l'e-mail");
		emailField.getStyleClass().add("text-field");
		emailField.setMaxWidth(280);
		emailBox.getChildren().addAll(emailLabel, emailField);
		emailBox.getStyleClass().add("field-box");
		
		VBox nomeBox = new VBox();
		Text nomeLabel = new Text("NOME");
		nomeLabel.getStyleClass().add("label");
		TextField nomeField = new TextField();
		nomeField.setPromptText("Inserisci il nominativo");
		nomeField.getStyleClass().add("text-field");
		nomeField.setMaxWidth(280);
		nomeBox.getChildren().addAll(nomeLabel, nomeField);
		nomeBox.getStyleClass().add("field-box");

		VBox telefonoBox = new VBox();
		Text telefonoLabel = new Text("NUMERO DI TELEFONO");
		telefonoLabel.getStyleClass().add("label");
		TextField telefonoField = new TextField();
		telefonoField.setPromptText("Inserisci il numero di telefono");
		telefonoField.getStyleClass().add("text-field");
		telefonoField.setMaxWidth(280);
		telefonoBox.getChildren().addAll(telefonoLabel, telefonoField);
		telefonoBox.getStyleClass().add("field-box");
		
		VBox passwordBox = new VBox();
		Text passwordLabel = new Text("PASSWORD");
		passwordLabel.getStyleClass().add("label");
		TextField passwordField = new PasswordField();
		passwordField.setPromptText("Inserisci la password");
		passwordField.getStyleClass().add("text-field");
		passwordField.setMaxWidth(280);
		passwordBox.getChildren().addAll(passwordLabel, passwordField);
		passwordBox.getStyleClass().add("password-box");

		userTypeComboBox = new ComboBox<>();
		userTypeComboBox.getItems().addAll("Cliente", "Titolare", "Corriere");
		userTypeComboBox.setPromptText("Seleziona tipo utente");

		registerButton = new Button("Registrati");
		registerButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		registerButton.setOnAction(e -> {
			try {
				handleRegistration();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

        VBox loginButtonBox = new VBox();
        Text loginLabel = new Text("Se hai già un account: ");
        loginLabel.getStyleClass().add("label");
        loginButtonBox.setAlignment(Pos.CENTER);
        loginButtonBox.setSpacing(5);
        loginButtonBox.setMaxWidth(280);
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> switchToLoginScreen());
        registerButton.getStyleClass().add("button-secondary");
        registerButton.setAlignment(Pos.CENTER);
        loginButtonBox.getChildren().addAll(loginLabel, loginButton);

		Button popolaDB = new Button("Popola il database");
		popolaDB.setOnAction(e -> PopolaDatabase.popolaDatabase());

		getChildren().addAll(logoView, titleRegistrazione, emailBox, nomeBox, telefonoBox, passwordBox, userTypeComboBox, registerButton, loginButtonBox, popolaDB);
	}


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
