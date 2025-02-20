package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import dao.RistoranteDAO;
import model.Ristorante;
import java.sql.SQLException;

public class ModificaRistorante extends VBox {
    private TextField nomeField, telefonoField, indirizzoField;
    private String emailTitolare = SessioneUtente.getEmail();
    private String nomeRistorante;
    private RistoranteDAO ristoranteDAO;

    public ModificaRistorante(String nomeRistorante) {
        super(10);
        this.nomeRistorante = nomeRistorante;
        this.setStyle("-fx-padding: 10;");

        try {
            this.ristoranteDAO = new RistoranteDAO();
        } catch (SQLException e) {
            showAlert("Errore", "Errore di connessione al database.");
            return;
        }

        // Titolo
        Label titolo = new Label("Modifica Ristorante: " + nomeRistorante);

        // Campi di input
        nomeField = new TextField();
        telefonoField = new TextField();
        indirizzoField = new TextField();

        // Carica i dati attuali
        caricaDatiRistorante();

        // Pulsante per salvare le modifiche
        Button salvaButton = new Button("Salva Modifiche");
        salvaButton.setOnAction(e -> salvaModifiche());
        
        // Pulsante per tornare alla gestione ristoranti
        Button tornaButton = new Button("Torna alla Gestione Ristoranti");
        tornaButton.setOnAction(e -> getScene().setRoot(new MainScreenTitolare()));

        // Aggiunta elementi al layout
        this.getChildren().addAll(titolo, nomeField, telefonoField, indirizzoField, salvaButton, tornaButton);
    }

    private void caricaDatiRistorante() {
        try {
            Ristorante ristorante = ristoranteDAO.getRistoranteByNome(nomeRistorante, emailTitolare);
            if (ristorante != null) {
                nomeField.setText(ristorante.getNome());
                telefonoField.setText(ristorante.getTelefono());
                indirizzoField.setText(ristorante.getIndirizzo());
            } else {
                showAlert("Errore", "Ristorante non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore nel caricamento dei dati del ristorante.");
        }
    }

    private void salvaModifiche() {
        String nuovoNome = nomeField.getText();
        String nuovoTelefono = telefonoField.getText();
        String nuovoIndirizzo = indirizzoField.getText();

        if (nuovoNome.isEmpty() || nuovoTelefono.isEmpty() || nuovoIndirizzo.isEmpty()) {
            showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        try {
            Ristorante ristorante = new Ristorante(nuovoNome, nuovoTelefono, nuovoIndirizzo, emailTitolare);
            boolean success = ristoranteDAO.aggiornaRistorante(ristorante ,nomeRistorante);
            
            if (success) {
                showAlert("Successo", "Ristorante modificato con successo!");
                getScene().setRoot(new MainScreenTitolare());
            } else {
                showAlert("Errore", "Modifica non riuscita.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante la modifica del ristorante.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
