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

    public ModificaRistorante(String nomeRistorante) {
        super(10);
        this.nomeRistorante = nomeRistorante;
        this.setStyle("-fx-padding: 10;");
        
        // Titolo grande
        Label titolo = new Label("Modifica Ristorante: " + nomeRistorante);
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Crea un HBox per il titolo (senza il pulsante torna indietro in alto)
        HBox header = new HBox(10);
        header.getChildren().addAll(titolo);
        header.setStyle("-fx-padding: 10;");
        
        // Campi di input con i rispettivi Label
        Label nomeLabel = new Label("Nome Pizzeria");
        nomeField = new TextField();
        nomeField.setPromptText("Nome pizzeria");
        
        Label telefonoLabel = new Label("Numero di Telefono");
        telefonoField = new TextField();
        telefonoField.setPromptText("Numero di telefono");
        
        Label indirizzoLabel = new Label("Indirizzo");
        indirizzoField = new TextField();
        indirizzoField.setPromptText("Indirizzo");

        // Carica i dati attuali
        caricaDatiRistorante();

        // Pulsante per salvare le modifiche
        Button salvaButton = new Button("Salva Modifiche");
        salvaButton.setOnAction(e -> salvaModifiche());
        
        // Pulsante per tornare alla gestione ristoranti
        Button tornaButton2 = new Button("Torna alla Gestione Ristoranti");
        tornaButton2.setOnAction(e -> getScene().setRoot(new MainScreenTitolare()));

        // Aggiunta degli elementi al layout
        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");
        buttonContainer.getChildren().addAll(salvaButton, tornaButton2);

        this.getChildren().addAll(header, nomeLabel, nomeField, telefonoLabel, telefonoField, indirizzoLabel, indirizzoField, buttonContainer);
    }

    private void caricaDatiRistorante() {
        try {
            Ristorante ristorante = RistoranteDAO.getInstance().getRistoranteByNome(nomeRistorante, emailTitolare);
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
            boolean success = RistoranteDAO.getInstance().aggiornaRistorante(ristorante ,nomeRistorante);
            
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
