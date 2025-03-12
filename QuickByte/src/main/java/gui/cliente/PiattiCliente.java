package gui.cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sessione.*;
import utilities.Utilities;
import dao.PiattoDAO;
import model.Piatto;
import com.pavlobu.emojitextflow.EmojiTextFlow;

import java.sql.SQLException;
import java.util.List;

public class PiattiCliente extends VBox {

    private String emailCliente;
    private String nomeMenu;
    private int idRistorante;
    private TableView<Piatto> tabellaPiatti;

    public PiattiCliente() throws SQLException {
        super(10);

        this.idRistorante = SessioneRistorante.getId();
        this.nomeMenu = SessioneMenu.getNome();
        this.emailCliente = SessioneUtente.getEmail();

        this.setStyle("-fx-padding: 10;");

        // Crea e configura l'interfaccia utente
        configuraInterfaccia();
    }

    /**
     * Configura l'interfaccia utente con titolo, tabella e pulsante di ritorno.
     */
    private void configuraInterfaccia() {
        Label titolo = new Label("Piatti del menù: " + nomeMenu);
        titolo.getStyleClass().add("title");

        // Inizializza e carica la tabella dei piatti
        inizializzaTabella();
        caricaPiatti();

        Button bottoneIndietro = new Button("⬅ INDIETRO");
        bottoneIndietro.setOnAction(event -> tornaIndietro());

        // Aggiunge gli elementi alla scena
        this.getChildren().addAll(titolo, tabellaPiatti, bottoneIndietro);
    }

    /**
     * Inizializza la tabella dei piatti con le colonne necessarie.
     */
    private void inizializzaTabella() {
        tabellaPiatti = new TableView<>();
        tabellaPiatti.getStyleClass().add("table-view");

        // Configura le colonne della tabella
        configuraColonne();
    }

    /**
     * Configura le colonne della tabella dei piatti.
     */
    private void configuraColonne() {
        TableColumn<Piatto, String> colonnaNome = new TableColumn<>("Piatto");
        colonnaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Piatto, Double> colonnaCosto = new TableColumn<>("Costo (€)");
        colonnaCosto.setCellValueFactory(new PropertyValueFactory<>("prezzo"));

        TableColumn<Piatto, String> colonnaAllergeni = new TableColumn<>("Allergeni");
        colonnaAllergeni.setCellValueFactory(new PropertyValueFactory<>("allergeni"));

        TableColumn<Piatto, Void> colonnaVediFoto = creaColonnaVediFoto();
        TableColumn<Piatto, Void> colonnaAggiungiAlCarrello = creaColonnaAggiungiAlCarrello();

        // Aggiunge tutte le colonne alla tabella
        tabellaPiatti.getColumns().addAll(colonnaNome, colonnaCosto, colonnaAllergeni, colonnaVediFoto, colonnaAggiungiAlCarrello);
    }

    /**
     * Crea la colonna "Vedi Foto" con il pulsante per visualizzare la foto del piatto.
     *
     * @return La colonna configurata.
     */
    private TableColumn<Piatto, Void> creaColonnaVediFoto() {
        TableColumn<Piatto, Void> colonna = new TableColumn<>("Vedi Foto");
        colonna.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button bottoneFoto = new Button();
            private final EmojiTextFlow emojiTextFlow = new EmojiTextFlow();

            {
                emojiTextFlow.parseAndAppend(":camera:");
                bottoneFoto.setGraphic(emojiTextFlow);
                bottoneFoto.getStyleClass().add("table-button-emoji");

                bottoneFoto.setOnAction(event -> {
                    Piatto piatto = getTableRow().getItem();
                    if (piatto != null) {
                        mostraDialogoFoto(piatto.getFoto());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(bottoneFoto);
                }
            }
        });
        return colonna;
    }

    /**
     * Crea la colonna "Aggiungi al carrello" con il pulsante per aggiungere il piatto al carrello.
     *
     * @return La colonna configurata.
     */
    private TableColumn<Piatto, Void> creaColonnaAggiungiAlCarrello() {
        TableColumn<Piatto, Void> colonna = new TableColumn<>("Aggiungi al carrello");
        colonna.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button bottoneCarrello = new Button("");
            private final EmojiTextFlow emojiTextFlow = new EmojiTextFlow();

            {
                emojiTextFlow.parseAndAppend(":shopping_cart:");
                bottoneCarrello.setGraphic(emojiTextFlow);
                bottoneCarrello.getStyleClass().add("table-button-emoji");

                bottoneCarrello.setOnAction(event -> {
                    Piatto piatto = getTableView().getItems().get(getIndex());
                    try {
                        aggiungiAlCarrello(piatto.getIdPiatto());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Utilities.showAlert("Errore", "Errore nell'aggiunta al carrello.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(bottoneCarrello);
                }
            }
        });
        return colonna;
    }

    /**
     * Carica i piatti dal database e li aggiunge alla tabella.
     */
    private void caricaPiatti() {
        try {
            List<Piatto> piatti = PiattoDAO.getInstance().getPiattiByMenuAndIdRistoranteDisponibili(nomeMenu, idRistorante);
            ObservableList<Piatto> listaPiatti = FXCollections.observableArrayList(piatti);
            tabellaPiatti.setItems(listaPiatti);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento dei piatti.");
        }
    }

    /**
     * Aggiunge un piatto al carrello dell'utente.
     *
     * @param idPiatto L'ID del piatto da aggiungere.
     * @throws SQLException Se si verifica un errore durante l'aggiunta al carrello.
     */
    private void aggiungiAlCarrello(int idPiatto) throws SQLException {
        boolean carrelloPieno = SessioneCarrello.getPieno();
        int idRistoranteCarrello = SessioneCarrello.getIdRistorante();

        if (!carrelloPieno) {
            SessioneCarrello.setIdRistorante(idRistorante);
            SessioneCarrello.setPieno(true);
            PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
        } else if (idRistoranteCarrello == idRistorante) {
            PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
        } else {
            mostraPopupConferma(idPiatto);
        }
    }

    /**
     * Mostra un popup di conferma per svuotare il carrello e aggiungere un nuovo piatto.
     *
     * @param idPiatto L'ID del piatto da aggiungere.
     */
    private void mostraPopupConferma(int idPiatto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attenzione");
        alert.setHeaderText("Hai già piatti di un altro ristorante nel carrello.");
        alert.setContentText("Vuoi svuotare il carrello e procedere con il nuovo ristorante?");

        ButtonType bottoneProcedi = new ButtonType("Procedi");
        ButtonType bottoneAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bottoneProcedi, bottoneAnnulla);

        alert.showAndWait().ifPresent(response -> {
            if (response == bottoneProcedi) {
                try {
                    PiattoDAO.getInstance().svuotaCarrello(emailCliente);
                    SessioneCarrello.setPieno(true);
                    SessioneCarrello.setIdRistorante(idRistorante);
                    PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Mostra una finestra di dialogo con la foto del piatto.
     *
     * @param fotoUrl L'URL della foto del piatto.
     */
    private void mostraDialogoFoto(String fotoUrl) {
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            String percorsoImmagine = "/images/" + fotoUrl;
            ImageView imageView = new ImageView(new Image(getClass().getResource(percorsoImmagine).toExternalForm()));
            imageView.setFitWidth(300);
            imageView.setFitHeight(200);
            StackPane stackPane = new StackPane(imageView);
            Scene scenaFoto = new Scene(stackPane, 400, 300);

            Stage finestraFoto = new Stage();
            finestraFoto.setTitle("Foto del piatto");
            finestraFoto.setScene(scenaFoto);
            finestraFoto.show();
        } else {
            Utilities.showAlert("Errore", "Foto non disponibile per questo piatto.");
        }
    }

    /**
     * Torna alla schermata precedente.
     */
    private void tornaIndietro() {
        MenuCliente schermataMenuCliente = new MenuCliente();
        this.getScene().setRoot(schermataMenuCliente);
    }
}
