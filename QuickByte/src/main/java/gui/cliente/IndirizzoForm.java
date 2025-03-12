package gui.cliente;

import dao.IndirizzoDAO;
import database.DatabaseConnection;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import model.Indirizzo;
import java.sql.Connection;
import java.sql.SQLException;
import sessione.*;
import utilities.Utilities;

public class IndirizzoForm extends VBox {

    private String emailUtente;

    /**
     * Costruttore della classe IndirizzoForm.
     * Inizializza la vista e imposta i campi per l'inserimento di un nuovo indirizzo.
     */
    public IndirizzoForm() {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        impostaAllineamento();
        aggiungiComponenti();
    }

    /**
     * Imposta l'allineamento centrale per la vista.
     */
    private void impostaAllineamento() {
        setAlignment(Pos.CENTER);
    }

    /**
     * Aggiunge i componenti dell'interfaccia utente alla vista.
     */
    private void aggiungiComponenti() {
        Label titolo = creaTitolo();
        TextField campoIndirizzo = creaCampoTesto("Indirizzo");
        TextField campoCitta = creaCampoTesto("Città");
        TextField campoCap = creaCampoTesto("CAP");
        TextField campoProvincia = creaCampoTesto("Provincia");

        Button bottoneSalva = creaBottoneSalva(campoIndirizzo, campoCitta, campoCap, campoProvincia);
        Button bottoneAnnulla = creaBottoneAnnulla();

        getChildren().addAll(titolo, campoIndirizzo, campoCitta, campoCap, campoProvincia, bottoneSalva, bottoneAnnulla);
    }

    /**
     * Crea il titolo della vista.
     *
     * @return Il titolo creato.
     */
    private Label creaTitolo() {
        return Utilities.createLabel("Aggiungi un indirizzo", "title");
    }

    /**
     * Crea un campo di testo con un prompt specificato.
     *
     * @param prompt Il testo del prompt da visualizzare nel campo.
     * @return Il campo di testo creato.
     */
    private TextField creaCampoTesto(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        return campo;
    }

    /**
     * Crea il bottone per salvare l'indirizzo.
     *
     * @param campoIndirizzo Il campo di testo per l'indirizzo.
     * @param campoCitta     Il campo di testo per la città.
     * @param campoCap       Il campo di testo per il CAP.
     * @param campoProvincia Il campo di testo per la provincia.
     * @return Il bottone creato.
     */
    private Button creaBottoneSalva(TextField campoIndirizzo, TextField campoCitta, TextField campoCap, TextField campoProvincia) {
        return Utilities.createButton("Salva indirizzo", () -> {
            String indirizzo = campoIndirizzo.getText();
            String citta = campoCitta.getText();
            String cap = campoCap.getText();
            String provincia = campoProvincia.getText();

            if (campiVuoti(indirizzo, citta, cap, provincia)) {
                Utilities.showAlert("Errore", "Tutti i campi sono obbligatori.");
                return;
            }

            Indirizzo nuovoIndirizzo = new Indirizzo(0, indirizzo, citta, cap, provincia, emailUtente);
            salvaIndirizzo(nuovoIndirizzo);
        });
    }

    /**
     * Verifica se uno dei campi è vuoto.
     *
     * @param campi I campi da verificare.
     * @return true se almeno uno dei campi è vuoto, false altrimenti.
     */
    private boolean campiVuoti(String... campi) {
        for (String campo : campi) {
            if (campo.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea il bottone per annullare l'operazione.
     *
     * @return Il bottone creato.
     */
    private Button creaBottoneAnnulla() {
        return Utilities.createButton("Annulla", this::tornaIndietro);
    }

    /**
     * Salva l'indirizzo nel database.
     *
     * @param indirizzo L'indirizzo da salvare.
     */
    private void salvaIndirizzo(Indirizzo indirizzo) {
        try (Connection conn = DatabaseConnection.connect()) {
            IndirizzoDAO.getInstance().aggiungiIndirizzo(indirizzo);
            Utilities.showAlert("Successo", "Indirizzo salvato correttamente!");
            tornaIndietro();
        } catch (SQLException e) {
            Utilities.showAlert("Errore", "Errore nel salvataggio dell'indirizzo.");
            e.printStackTrace();
        }
    }

    /**
     * Torna alla schermata precedente.
     */
    private void tornaIndietro() {
        CarrelloView schermataPrincipaleCliente = new CarrelloView();
        Scene scenaCorrente = this.getScene();
        scenaCorrente.setRoot(schermataPrincipaleCliente);
    }
}
