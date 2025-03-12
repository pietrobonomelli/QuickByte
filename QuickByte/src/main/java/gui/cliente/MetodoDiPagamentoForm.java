package gui.cliente;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sessione.SessioneUtente;
import utilities.Utilities;
import javafx.scene.Scene;
import java.sql.SQLException;
import dao.MetodoDiPagamentoDAO;
import model.MetodoDiPagamento;

/**
 * Classe che rappresenta il form per aggiungere un metodo di pagamento.
 */
public class MetodoDiPagamentoForm extends VBox {

    private String emailUtente;

    /**
     * Costruttore del form per aggiungere un metodo di pagamento.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    public MetodoDiPagamentoForm() throws SQLException {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        setAlignment(Pos.CENTER);

        Label titolo = new Label("Aggiungi un metodo di pagamento");

        TextField campoNominativo = Utilities.creaCampoTesto("Nome sulla carta");
        TextField campoNumeroCarta = Utilities.creaCampoTesto("Numero carta (16 cifre)");
        TextField campoScadenza = Utilities.creaCampoTesto("Scadenza (MM/YY)");

        Button pulsanteSalva = new Button("Salva carta");
        pulsanteSalva.setOnAction(e -> gestisciSalvataggio(campoNominativo, campoNumeroCarta, campoScadenza));

        Button pulsanteAnnulla = new Button("Annulla");
        pulsanteAnnulla.setOnAction(e -> tornaIndietro());

        getChildren().addAll(titolo, campoNominativo, campoNumeroCarta, campoScadenza, pulsanteSalva, pulsanteAnnulla);
    }

    /**
     * Gestisce il salvataggio del metodo di pagamento.
     *
     * @param campoNominativo Il campo del nominativo.
     * @param campoNumeroCarta Il campo del numero della carta.
     * @param campoScadenza Il campo della scadenza.
     */
    private void gestisciSalvataggio(TextField campoNominativo, TextField campoNumeroCarta, TextField campoScadenza) {
        String nominativo = campoNominativo.getText();
        String numeroCarta = campoNumeroCarta.getText();
        String scadenza = campoScadenza.getText();

        if (nominativo.isEmpty() || numeroCarta.isEmpty() || scadenza.isEmpty()) {
            Utilities.showAlert("Errore", "Tutti i campi sono obbligatori.");
            return;
        }

        if (!validaNumeroCarta(numeroCarta)) {
            Utilities.showAlert("Errore", "Il numero della carta deve avere 16 cifre.");
            return;
        }

        MetodoDiPagamento metodo = new MetodoDiPagamento(nominativo, numeroCarta, scadenza, emailUtente);
        salvaMetodoDiPagamento(metodo);
    }

    /**
     * Valida il numero della carta.
     *
     * @param numeroCarta Il numero della carta da validare.
     * @return True se il numero è valido, altrimenti false.
     */
    private boolean validaNumeroCarta(String numeroCarta) {
        return numeroCarta.length() == 16 && numeroCarta.matches("\\d+");
    }

    /**
     * Salva il metodo di pagamento nel database.
     *
     * @param metodo Il metodo di pagamento da salvare.
     */
    private void salvaMetodoDiPagamento(MetodoDiPagamento metodo) {
        try {
            MetodoDiPagamentoDAO.getInstance().aggiungiMetodo(metodo);
            Utilities.showAlert("Successo", "Metodo di pagamento salvato correttamente!");
            tornaIndietro();
        } catch (SQLException e) {
            Utilities.showAlert("Errore", "Errore nel salvataggio del metodo di pagamento.");
            e.printStackTrace();
        }
    }

    /**
     * Torna alla schermata precedente.
     */
    private void tornaIndietro() {
        CarrelloView schermataCarrello = new CarrelloView();
        Scene scenaCorrente = this.getScene();
        scenaCorrente.setRoot(schermataCarrello);
    }
}
