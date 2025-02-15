package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import database.DatabaseConnection;
import gui.main.SessioneUtente;

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
        confermaButton.setOnAction(e -> inserisciRistorante());

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

    private void inserisciRistorante() {
        // Recupera i dati dai campi di input
        String nome = nomeRistoranteField.getText();
        String telefono = telefonoField.getText();
        String indirizzo = indirizzoField.getText();

        if (nome.isEmpty() || telefono.isEmpty() || indirizzo.isEmpty()) {
            // Mostra un messaggio di errore se qualche campo è vuoto
            showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        // Connessione al database e inserimento dei dati
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO Ristorante (nome, telefono, indirizzo, emailTitolare) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nome);
                stmt.setString(2, telefono);
                stmt.setString(3, indirizzo);
                stmt.setString(4, emailTitolare);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Successo, mostra un messaggio di conferma
                    showAlert("Successo", "Ristorante inserito con successo!");
                    
                    // Torna alla schermata di gestione ristoranti
                    getScene().setRoot(new MainScreenTitolare());

                } else {
                    showAlert("Errore", "Impossibile inserire il ristorante.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
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
