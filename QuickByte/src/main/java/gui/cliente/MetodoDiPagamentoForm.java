package gui.cliente;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sessione.SessioneUtente;
import javafx.scene.Scene;

import java.sql.SQLException;

import dao.MetodoDiPagamentoDAO;
import model.MetodoDiPagamento;

public class MetodoDiPagamentoForm extends VBox {

    private String emailUtente;

    public MetodoDiPagamentoForm() throws SQLException {
        super(10);
        this.emailUtente = SessioneUtente.getEmail();
        setAlignment(Pos.CENTER);

        Label titolo = new Label("Aggiungi un metodo di pagamento");

        TextField nominativoField = new TextField();
        nominativoField.setPromptText("Nome sulla carta");

        TextField numeroCartaField = new TextField();
        numeroCartaField.setPromptText("Numero carta (16 cifre)");

        TextField scadenzaField = new TextField();
        scadenzaField.setPromptText("Scadenza (MM/YY)");

        Button salvaButton = new Button("Salva carta");
        salvaButton.setOnAction(e -> {
            String nominativo = nominativoField.getText();
            String numeroCarta = numeroCartaField.getText();
            String scadenza = scadenzaField.getText();

            if (nominativo.isEmpty() || numeroCarta.isEmpty() || scadenza.isEmpty()) {
                showAlert("Errore", "Tutti i campi sono obbligatori.");
                return;
            }

            if (numeroCarta.length() != 16 || !numeroCarta.matches("\\d+")) {
                showAlert("Errore", "Il numero della carta deve avere 16 cifre.");
                return;
            }

            MetodoDiPagamento metodo = new MetodoDiPagamento(nominativo, numeroCarta, scadenza, emailUtente);
            salvaMetodoDiPagamento(metodo);
        });

        Button annullaButton = new Button("Annulla");
        annullaButton.setOnAction(e -> tornaIndietro());

        getChildren().addAll(titolo, nominativoField, numeroCartaField, scadenzaField, salvaButton, annullaButton);
    }

    private void salvaMetodoDiPagamento(MetodoDiPagamento metodo) {
        try {
            MetodoDiPagamentoDAO.getInstance().aggiungiMetodo(metodo);
            showAlert("Successo", "Metodo di pagamento salvato correttamente!");
            tornaIndietro(); // Torna alla schermata precedente
        } catch (SQLException e) {
            showAlert("Errore", "Errore nel salvataggio del metodo di pagamento.");
            e.printStackTrace();
        }
    }

    private void tornaIndietro() {
        CarrelloView mainClienteScreen = new CarrelloView();  // Torna alla schermata precedente
        Scene currentScene = this.getScene();
        currentScene.setRoot(mainClienteScreen);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
