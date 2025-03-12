package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import utilities.Utilities;
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
        Label titolo = Utilities.createLabel("Inserisci Ristorante", "title-label");
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Crea i campi di inserimento con le Label sopra
        nomeRistoranteField = new TextField();
        VBox nomeBox = Utilities.createFieldBox("Nome Ristorante", "Nome del ristorante", nomeRistoranteField);

        telefonoField = new TextField();
        VBox telefonoBox = Utilities.createFieldBox("Numero di Telefono", "Numero di telefono", telefonoField);

        indirizzoField = new TextField();
        VBox indirizzoBox = Utilities.createFieldBox("Indirizzo", "Indirizzo", indirizzoField);

        // Mostra l'email del titolare ma non consentire la modifica
        emailTitolareLabel = Utilities.createLabel("Email Titolare: " + emailTitolare, "email-label");

        // Pulsante per confermare l'inserimento
        Button confermaButton = Utilities.createButton("Inserisci Ristorante", this::inserisciRistorante);

        // Pulsante per tornare alla schermata di gestione ristoranti
        Button tornaButton = Utilities.createButton("Torna alla gestione ristoranti", this::tornaAllaGestioneRistoranti);

        // Aggiungi i pulsanti in una HBox (vicini)
        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");
        buttonContainer.getChildren().addAll(confermaButton, tornaButton);

        // Aggiungi tutti gli elementi al layout
        this.getChildren().addAll(
            titolo,
            nomeBox,
            telefonoBox,
            indirizzoBox,
            emailTitolareLabel,
            buttonContainer
        );
    }

    private void inserisciRistorante() {
        // Recupera i dati dai campi di input
        String nome = nomeRistoranteField.getText();
        String telefono = telefonoField.getText();
        String indirizzo = indirizzoField.getText();

        if (nome.isEmpty() || telefono.isEmpty() || indirizzo.isEmpty()) {
            // Mostra un messaggio di errore se qualche campo è vuoto
            Utilities.showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        try {
            // Utilizza il DAO per inserire il ristorante nel database
            RistoranteDAO.getInstance().inserisciRistorante(nome, telefono, indirizzo, emailTitolare);

            // Successo, mostra un messaggio di conferma
            Utilities.showAlert("Successo", "Ristorante inserito con successo!");

            // Torna alla schermata di gestione ristoranti
            getScene().setRoot(new MainScreenTitolare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tornaAllaGestioneRistoranti() {
        // Torna alla schermata di gestione ristoranti
        getScene().setRoot(new MainScreenTitolare());
    }
}
