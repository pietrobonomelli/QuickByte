package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.SessioneUtente;
import utilities.Utilities;
import dao.RistoranteDAO;

import java.sql.SQLException;

public class InserisciRistorante extends VBox {

    private TextField campoNomeRistorante, campoTelefono, campoIndirizzo;
    private Label etichettaEmailTitolare;
    private String emailTitolare = SessioneUtente.getEmail();

    public InserisciRistorante() {
        super(10); // Imposta il padding tra i componenti
        this.setStyle("-fx-padding: 10;");

        Label titolo = Utilities.createLabel("Inserisci Ristorante", "title");

        // Creazione dei campi di inserimento con le etichette sopra
        campoNomeRistorante = new TextField();
        VBox boxNome = Utilities.createFieldBox("Nome Ristorante", "Nome del ristorante", campoNomeRistorante);

        campoTelefono = new TextField();
        VBox boxTelefono = Utilities.createFieldBox("Numero di Telefono", "Numero di telefono", campoTelefono);

        campoIndirizzo = new TextField();
        VBox boxIndirizzo = Utilities.createFieldBox("Indirizzo", "Indirizzo", campoIndirizzo);


        etichettaEmailTitolare = Utilities.createLabel("Email Titolare: " + emailTitolare, "email-label");
      
        Button bottoneConferma = Utilities.createButton("Inserisci Ristorante", this::inserisciRistorante);
        Button bottoneTorna = Utilities.createButton("Torna alla gestione ristoranti", this::tornaAllaGestioneRistoranti);
        HBox contenitoreBottoni = new HBox(10);
        contenitoreBottoni.setStyle("-fx-padding: 10;");
        contenitoreBottoni.getChildren().addAll(bottoneConferma, bottoneTorna);

        // Aggiungi tutti gli elementi al layout
        this.getChildren().addAll(
            titolo,
            boxNome,
            boxTelefono,
            boxIndirizzo,
            etichettaEmailTitolare,
            contenitoreBottoni
        );
    }

    /**
     * Inserisce un nuovo ristorante nel database.
     */
    private void inserisciRistorante() {
        // Recupera i dati dai campi di input
        String nome = campoNomeRistorante.getText();
        String telefono = campoTelefono.getText();
        String indirizzo = campoIndirizzo.getText();

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
            tornaAllaGestioneRistoranti();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'inserimento del ristorante.");
        }
    }

    /**
     * Torna alla schermata di gestione ristoranti.
     */
    private void tornaAllaGestioneRistoranti() {
        // Torna alla schermata di gestione ristoranti
        getScene().setRoot(new MainScreenTitolare());
    }
}
