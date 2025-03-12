package gui.cliente;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import sessione.*;
import utilities.Utilities;
import dao.*;
import model.*;
import java.sql.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

/**
 * Classe che rappresenta la vista del carrello del cliente.
 */
public class CarrelloView extends VBox {

    private String emailUtente;

    /**
     * Costruttore della vista del carrello del cliente.
     */
    public CarrelloView() {
        super(10);
        this.setStyle("-fx-padding: 10;");

        this.emailUtente = SessioneUtente.getEmail();

        Label titolo = Utilities.createLabel("Carrello del Cliente", "title");
        this.getChildren().add(titolo);

        caricaCarrello();
    }

    /**
     * Carica il carrello del cliente nella vista.
     */
    private void caricaCarrello() {
        rimuoviElementiPrecedenti();

        TableView<Carrello> tabella = creaTabellaCarrello();

        Button pulsanteHome = Utilities.createButton("⬅ HOME", this::tornaAllaHome);
        Button pulsanteConfermaOrdine = Utilities.createButton("CONFERMA ORDINE", () -> {
            try {
                confermaOrdine();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        HBox boxPulsanti = new HBox(10, pulsanteHome, pulsanteConfermaOrdine);

        this.getChildren().addAll(tabella, boxPulsanti);
    }

    /**
     * Rimuove gli elementi precedenti dalla vista, mantenendo il titolo.
     */
    private void rimuoviElementiPrecedenti() {
        this.getChildren().removeIf(node -> node instanceof TableView || node instanceof HBox);
    }

    /**
     * Crea e configura la tabella del carrello.
     *
     * @return La tabella configurata.
     */
    private TableView<Carrello> creaTabellaCarrello() {
        TableView<Carrello> tabella = new TableView<>();
        TableColumn<Carrello, String> colonnaPiatto = new TableColumn<>("Piatto");
        TableColumn<Carrello, Void> colonnaAzioni = new TableColumn<>("Azioni");
        tabella.getStyleClass().add("table-view");

        colonnaPiatto.setCellValueFactory(data -> {
            String nomePiatto = "";
            try {
                nomePiatto = CarrelloDAO.getInstance().getNomePiattoById(data.getValue().getIdPiatto());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(nomePiatto + " (x" + data.getValue().getQuantitaPiatti() + ")");
        });

        colonnaAzioni.setCellFactory(param -> new TableCell<Carrello, Void>() {
            private final Button pulsanteAggiungi = Utilities.createButtonEmoji("", ":heavy_plus_sign:", () -> modificaQuantita(getTableRow().getItem(), 1));
            private final Button pulsanteRimuovi = Utilities.createButtonEmoji("", ":heavy_minus_sign:", () -> modificaQuantita(getTableRow().getItem(), -1));

            private final HBox boxPulsanti = new HBox(5, pulsanteRimuovi, pulsanteAggiungi);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(boxPulsanti);
                }
            }
        });

        tabella.getColumns().addAll(colonnaPiatto, colonnaAzioni);

        try {
            List<Carrello> carrelli = CarrelloDAO.getInstance().getCarrelloByUtente(emailUtente);
            tabella.getItems().addAll(carrelli);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento del carrello.");
        }

        return tabella;
    }

    /**
     * Modifica la quantità di un piatto nel carrello.
     *
     * @param item Il piatto da modificare.
     * @param delta La variazione della quantità.
     */
    private void modificaQuantita(Carrello item, int delta) {
        try {
            int nuovaQuantita = item.getQuantitaPiatti() + delta;
            if (nuovaQuantita > 0) {
                CarrelloDAO.getInstance().aggiornaQuantita(item.getIdCarrello(), nuovaQuantita);
            } else {
                rimuoviDalCarrello(item.getIdCarrello());
            }
            caricaCarrello();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante la modifica della quantità.");
        }
    }

    /**
     * Rimuove un piatto dal carrello.
     *
     * @param idCarrello L'ID del piatto da rimuovere.
     */
    private void rimuoviDalCarrello(int idCarrello) {
        try {
            CarrelloDAO.getInstance().rimuoviDalCarrello(idCarrello);
            caricaCarrello();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante la rimozione dal carrello.");
        }
    }

    /**
     * Torna alla schermata principale.
     */
    private void tornaAllaHome() {
        MainScreenCliente schermataPrincipale = new MainScreenCliente();
        this.getScene().setRoot(schermataPrincipale);
    }

    /**
     * Ottiene la data e l'ora corrente.
     *
     * @return La data e l'ora corrente formattata.
     */
    public String getDataOraCorrente() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * Conferma l'ordine del cliente.
     *
     * @throws SQLException Se si verifica un errore SQL.
     */
    private void confermaOrdine() throws SQLException {
        if (SessioneCarrello.getPieno()) {
            MetodoDiPagamentoDAO pagamentoDAO = new MetodoDiPagamentoDAO();
            List<String> metodiPagamento = pagamentoDAO.getMetodiPagamento(emailUtente);

            ChoiceDialog<String> sceltaCarta = new ChoiceDialog<>("Aggiungi metodo di pagamento", metodiPagamento);
            sceltaCarta.setTitle("Metodo di Pagamento");
            sceltaCarta.setHeaderText("Seleziona un metodo di pagamento:");
            sceltaCarta.setContentText("Metodo di pagamento:");

            sceltaCarta.showAndWait().ifPresent(cartaSelezionata -> {
                if (cartaSelezionata.equals("Aggiungi metodo di pagamento")) {
                    try {
                        this.getChildren().setAll(new MetodoDiPagamentoForm());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    selezionaIndirizzo();
                }
            });
        } else {
            Utilities.showAlert("Errore", "Carrello vuoto: impossibile effettuare l'ordine");
        }
    }

    /**
     * Seleziona l'indirizzo di consegna.
     */
    private void selezionaIndirizzo() {
        List<String> indirizzi = IndirizzoDAO.getInstance().getIndirizzi(emailUtente);

        ChoiceDialog<String> sceltaIndirizzo = new ChoiceDialog<>("Aggiungi indirizzo", indirizzi);
        sceltaIndirizzo.setTitle("Indirizzo di Consegna");
        sceltaIndirizzo.setHeaderText("Seleziona un indirizzo di consegna:");
        sceltaIndirizzo.setContentText("Indirizzo:");

        sceltaIndirizzo.showAndWait().ifPresent(indirizzoSelezionato -> {
            if (indirizzoSelezionato.equals("Aggiungi indirizzo")) {
                this.getChildren().setAll(new IndirizzoForm());
            } else {
                registraOrdine(indirizzoSelezionato);
            }
        });
    }

    /**
     * Registra l'ordine del cliente.
     *
     * @param indirizzo L'indirizzo di consegna.
     */
    private void registraOrdine(String indirizzo) {
        boolean successo = OrdineDAO.getInstance().registraOrdine(emailUtente, indirizzo);

        if (successo) {
            Utilities.showAlert("Ordine Confermato", "Il tuo ordine è stato pagato con successo!");
            caricaCarrello();
        } else {
            Utilities.showAlert("Errore", "Errore durante la conferma dell'ordine.");
        }
    }
}
