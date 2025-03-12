package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.*;
import model.*;
import sessione.SessioneRistorante;
import sessione.SessioneUtente;
import utilities.Utilities;
import java.sql.SQLException;
import gui.main.*;

public class MainScreenTitolare extends VBox {

    private String email;
    private TableView<Ristorante> tabellaRistoranti;

    public MainScreenTitolare() {
        super(10);
        this.email = SessioneUtente.getEmail();
        this.setStyle("-fx-padding: 10;");

        Label titolo = Utilities.createLabel("Gestione Ristoranti", "title");

        tabellaRistoranti = new TableView<>();
        tabellaRistoranti.getStyleClass().add("table-view");
        configuraTabella();

        HBox contenitoreBottoni = new HBox(10);
        contenitoreBottoni.setStyle("-fx-padding: 10;");
        Button bottoneInserisciRistorante = Utilities.createButton("Inserisci nuovo Ristorante", this::passaAInserisciRistorante);
        Button bottoneLogout = Utilities.createButtonLogout("Logout", this::passaASchermataLogin);

        contenitoreBottoni.getChildren().addAll(bottoneLogout, bottoneInserisciRistorante);

        this.getChildren().addAll(titolo, tabellaRistoranti, contenitoreBottoni);

        caricaRistoranti();
    }

    /**
     * Configura la tabella dei ristoranti con le colonne necessarie.
     */
    private void configuraTabella() {
        TableColumn<Ristorante, String> colonnaNome = new TableColumn<>("Gestisci menu ed ordinazioni");
        colonnaNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        TableColumn<Ristorante, Void> colonnaVisualizzaMenu = creaColonnaAzioni(":clipboard:", this::passaASchermataMenuTitolare);
        TableColumn<Ristorante, Void> colonnaModifica = creaColonnaAzioni(":pencil:", this::passaASchermataModificaRistorante);
        TableColumn<Ristorante, Void> colonnaElimina = creaColonnaAzioni(":wastebasket:", this::confermaEliminazione);

        tabellaRistoranti.getColumns().addAll(colonnaNome, colonnaVisualizzaMenu, colonnaModifica, colonnaElimina);
    }

    /**
     * Crea una colonna di azioni con un pulsante emoji.
     *
     * @param emoji L'emoji da visualizzare sul pulsante.
     * @param azione L'azione da eseguire al click del pulsante.
     * @return La colonna configurata.
     */
    private TableColumn<Ristorante, Void> creaColonnaAzioni(String emoji, AzioneSuRistorante azione) {
        return new TableColumn<Ristorante, Void>("") {{
            setCellFactory(param -> new TableCell<Ristorante, Void>() {
                private final Button bottone = Utilities.createButtonEmoji("", emoji, () -> {
                    Ristorante ristorante = getTableView().getItems().get(getIndex());
                    azione.esegui(ristorante);
                });

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : bottone);
                }
            });
        }};
    }

    /**
     * Carica i ristoranti nella tabella.
     */
    private void caricaRistoranti() {
        try {
            ObservableList<Ristorante> ristoranti = FXCollections.observableArrayList(RistoranteDAO.getInstance().getRistorantiByEmail(this.email));
            tabellaRistoranti.setItems(ristoranti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante il caricamento dei ristoranti.");
        }
    }

    /**
     * Passa alla schermata di inserimento di un nuovo ristorante.
     */
    private void passaAInserisciRistorante() {
        this.getScene().setRoot(new InserisciRistorante());
    }

    /**
     * Passa alla schermata di modifica di un ristorante.
     */
    private void passaASchermataModificaRistorante(Ristorante ristorante) {
        this.getScene().setRoot(new ModificaRistorante(ristorante.getNome()));
    }

    /**
     * Passa alla schermata di gestione del menu di un ristorante.
     */
    private void passaASchermataMenuTitolare(Ristorante ristorante) {
        SessioneRistorante.setId(ristorante.getIdRistorante()); // Imposta l'ID del ristorante nella sessione
        this.getScene().setRoot(new MenuTitolare());
    }

    /**
     * Passa alla schermata di login.
     */
    private void passaASchermataLogin() {
        this.getScene().setRoot(new LoginScreen());
    }

    /**
     * Mostra una finestra di conferma per l'eliminazione di un ristorante.
     */
    private void confermaEliminazione(Ristorante ristorante) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare " + ristorante.getNome());
        alert.setContentText("Sei sicuro?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminaRistorante(ristorante);
            }
        });
    }

    /**
     * Elimina un ristorante dal database.
     */
    private void eliminaRistorante(Ristorante ristorante) {
        try {
            RistoranteDAO.getInstance().rimuoviRistorante(ristorante.getIdRistorante());
            Utilities.showAlert("Successo", "Ristorante eliminato con successo.");
            caricaRistoranti(); // Ricarica l'elenco dei ristoranti dopo l'eliminazione
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del ristorante.");
        }
    }

    /**
     * Interfaccia funzionale per eseguire un'azione su un ristorante.
     */
    @FunctionalInterface
    private interface AzioneSuRistorante {
        void esegui(Ristorante ristorante);
    }
}
