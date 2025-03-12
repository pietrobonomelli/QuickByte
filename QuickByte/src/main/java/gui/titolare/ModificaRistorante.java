package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import utilities.Utilities;
import dao.RistoranteDAO;
import model.Ristorante;
import java.sql.SQLException;

public class ModificaRistorante extends VBox {
    private TextField nomeField, telefonoField, indirizzoField;
    private String emailTitolare = SessioneUtente.getEmail();
    private String nomeRistorante;

    public ModificaRistorante(String nomeRistorante) {
        super(10);
        this.nomeRistorante = nomeRistorante;
        this.setStyle("-fx-padding: 10;");

        // Titolo grande
        Label titolo = Utilities.createLabel("Modifica Ristorante: " + nomeRistorante, "title");

        // Crea un HBox per il titolo
        HBox header = new HBox(10);
        header.getChildren().addAll(titolo);
        header.setStyle("-fx-padding: 10;");

        // Campi di input con i rispettivi Label
        nomeField = new TextField();
        VBox nomeBox = Utilities.createFieldBox("Nome Pizzeria", "Nome pizzeria", nomeField);

        telefonoField = new TextField();
        VBox telefonoBox = Utilities.createFieldBox("Numero di Telefono", "Numero di telefono", telefonoField);

        indirizzoField = new TextField();
        VBox indirizzoBox = Utilities.createFieldBox("Indirizzo", "Indirizzo", indirizzoField);

        // Carica i dati attuali
        caricaDatiRistorante();

        // Pulsante per salvare le modifiche
        Button salvaButton = Utilities.createButton("Salva Modifiche", this::salvaModifiche);

        // Pulsante per tornare alla gestione ristoranti
        Button tornaButton = Utilities.createButton("Torna alla Gestione Ristoranti", () -> getScene().setRoot(new MainScreenTitolare()));

        // Aggiunta degli elementi al layout
        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");
        buttonContainer.getChildren().addAll(salvaButton, tornaButton);

        this.getChildren().addAll(header, nomeBox, telefonoBox, indirizzoBox, buttonContainer);
    }

    private void caricaDatiRistorante() {
        try {
            Ristorante ristorante = RistoranteDAO.getInstance().getRistoranteByNome(nomeRistorante, emailTitolare);
            if (ristorante != null) {
                nomeField.setText(ristorante.getNome());
                telefonoField.setText(ristorante.getTelefono());
                indirizzoField.setText(ristorante.getIndirizzo());
            } else {
                Utilities.showAlert("Errore", "Ristorante non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento dei dati del ristorante.");
        }
    }

    private void salvaModifiche() {
        String nuovoNome = nomeField.getText();
        String nuovoTelefono = telefonoField.getText();
        String nuovoIndirizzo = indirizzoField.getText();

        if (nuovoNome.isEmpty() || nuovoTelefono.isEmpty() || nuovoIndirizzo.isEmpty()) {
            Utilities.showAlert("Errore", "Tutti i campi devono essere compilati.");
            return;
        }

        try {
            Ristorante ristorante = new Ristorante(nuovoNome, nuovoTelefono, nuovoIndirizzo, emailTitolare);
            boolean success = RistoranteDAO.getInstance().aggiornaRistorante(ristorante, nomeRistorante);

            if (success) {
                Utilities.showAlert("Successo", "Ristorante modificato con successo!");
                getScene().setRoot(new MainScreenTitolare());
            } else {
                Utilities.showAlert("Errore", "Modifica non riuscita.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante la modifica del ristorante.");
        }
    }
}
