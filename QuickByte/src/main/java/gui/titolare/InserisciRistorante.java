package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import database.DatabaseConnection;
import dao.RistoranteDAO;  // Importa la classe DAO che gestisce l'inserimento nel database

import java.sql.*;

public class InserisciRistorante extends VBox {

    private VBox container; // Contenitore per l'inserimento
    private TextField nomeRistoranteField, telefonoField, indirizzoField;
    private Label emailTitolareLabel;
    private String emailTitolare = SessioneUtente.getEmail();

    public InserisciRistorante() {
        super(10); // Imposta il padding tra i componenti
        this.setStyle("-fx-padding: 10;");

        // Crea i campi di inserimento
        nomeRistoranteField = new TextField();
        nomeRistoranteField.setPromptText("Nome del ristorante");

        telefonoField = new TextField();
        telefonoField.setPromptText("Numero di telefono");

        indirizzoField = new TextField();
        indirizzoField.setPromptText("Indirizzo");

        // Mostra l'email del titolare ma non consentire la modifica
        emailTitolareLabel = new Label("Email Titolare: " + emailTitolare);

        // Pulsante per confermare l'inserimento
        Button confermaButton = new Button("Inserisci Ristorante");
        confermaButton.setOnAction(e -> {
			try {
				inserisciRistorante();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        // Pulsante per tornare alla schermata di gestione ristoranti
        Button tornaButton = new Button("Torna alla gestione ristoranti");
        tornaButton.setOnAction(e -> tornaAllaGestioneRistoranti());

        // Aggiungi gli elementi al layout
        this.getChildren().addAll(
            nomeRistoranteField,
            telefonoField,
            indirizzoField,
            emailTitolareLabel,
            confermaButton,
            tornaButton
        );
    }

    private void inserisciRistorante() throws SQLException {
        // Recupera i dati dai campi di input
        String nome = nomeRistoranteField.getText();
        String telefono = telefonoField.getText();
        String indirizzo = indirizzoField.getText();
        String emailTitolare = SessioneUtente.getEmail(); // Recupera l'email del titolare dalla sessione

        if (nome.isEmpty() || telefono.isEmpty() || indirizzo.isEmpty()) {
            // Mostra un messaggio di errore se qualche campo è vuoto
            showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        // Utilizza il DAO per inserire il ristorante nel database
        RistoranteDAO ristoranteDAO = new RistoranteDAO();
        ristoranteDAO.inserisciRistorante(nome, telefono, indirizzo, emailTitolare);

        // Successo, mostra un messaggio di conferma
        showAlert("Successo", "Ristorante inserito con successo!");

        // Torna alla schermata di gestione ristoranti
        getScene().setRoot(new MainScreenTitolare());
    }


    private void tornaAllaGestioneRistoranti() {
        // Torna alla schermata di gestione ristoranti
        getScene().setRoot(new MainScreenTitolare());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getContainer() {
        return container; // Restituisce il VBox contenente la schermata di inserimento
    }
}
