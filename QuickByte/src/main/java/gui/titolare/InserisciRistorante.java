package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import dao.RistoranteDAO;
import java.sql.*;

public class InserisciRistorante extends VBox {

    private TextField nomeRistoranteField, telefonoField, indirizzoField;
    private Label emailTitolareLabel;
    private String emailTitolare = SessioneUtente.getEmail();

    public InserisciRistorante() {
        super(10); // Imposta il padding tra i componenti
        this.setStyle("-fx-padding: 10;");

        // Titolo grande
        Label titolo = new Label("Inserisci Ristorante");
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Crea i campi di inserimento con le Label sopra
        Label nomeLabel = new Label("Nome Ristorante");
        nomeRistoranteField = new TextField();
        nomeRistoranteField.setPromptText("Nome del ristorante");

        Label telefonoLabel = new Label("Numero di Telefono");
        telefonoField = new TextField();
        telefonoField.setPromptText("Numero di telefono");

        Label indirizzoLabel = new Label("Indirizzo");
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
                e1.printStackTrace();
            }
        });

        // Pulsante per tornare alla schermata di gestione ristoranti
        Button tornaButton = new Button("Torna alla gestione ristoranti");
        tornaButton.setOnAction(e -> tornaAllaGestioneRistoranti());

        // Aggiungi i pulsanti in una HBox (vicini)
        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");
        buttonContainer.getChildren().addAll(confermaButton, tornaButton);

        // Aggiungi tutti gli elementi al layout
        this.getChildren().addAll(
            titolo, 
            nomeLabel, nomeRistoranteField, 
            telefonoLabel, telefonoField, 
            indirizzoLabel, indirizzoField, 
            emailTitolareLabel, 
            buttonContainer
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
        RistoranteDAO.getInstance().inserisciRistorante(nome, telefono, indirizzo, emailTitolare);

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
}
