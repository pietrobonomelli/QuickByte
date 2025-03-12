package gui.titolare;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.*;
import database.DatabaseConnection;
import sessione.*;
import utilities.Utilities;
import model.*;
import model.Menu;
import java.sql.*;
import java.util.List;
import com.pavlobu.emojitextflow.EmojiTextFlow;

public class MenuTitolare extends VBox {

    private VBox container;
    private int ristorante;
    private TableView<Ordine> tableOrdini; // Variabile di istanza per la table

    public MenuTitolare() {
        super(10);
        this.ristorante = SessioneRistorante.getId();
        System.out.println("Ristorante passato a GestisciMenu: " + ristorante);
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadMenu();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("⬅ INDIETRO");
        tornaButton.setOnAction(e -> switchToMainScreenTitolare());
        buttonContainer.getChildren().add(tornaButton);

        this.getChildren().add(buttonContainer);

        loadOrdini();
    }

    private void loadMenu() {
        Label titleLabel = new Label("Menu");
        container.getChildren().add(titleLabel);

        // Creazione della TableView per il menu
        TableView<Menu> tableMenu = new TableView<>();
        tableMenu.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableMenu.getStyleClass().add("table-view");

        // Creazione delle colonne per la TableView
        TableColumn<Menu, String> colNomeMenu = new TableColumn<>("Nome Menu");
        colNomeMenu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Menu, Void> colModifica = new TableColumn<>("Modifica");
        colModifica.setCellFactory(param -> new TableCell<Menu, Void>() {
        	private final EmojiTextFlow emojiTextFlow1 = new EmojiTextFlow();
            private final Button modificaButton = new Button();
            {            	
            	emojiTextFlow1.parseAndAppend(":pencil:");
            	modificaButton.setGraphic(emojiTextFlow1);

            	modificaButton.getStyleClass().add("table-button-emoji");
                modificaButton.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    try {
                        switchToModificaMenu(menu.getNome());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(modificaButton);
                }
            }
        });

        TableColumn<Menu, Void> colElimina = new TableColumn<>("Elimina");
        colElimina.setCellFactory(param -> new TableCell<Menu, Void>() {
            private final Button eliminaButton = new Button();
            private final EmojiTextFlow emojiTextFlow2 = new EmojiTextFlow();
            {            	
            	emojiTextFlow2.parseAndAppend(":wastebasket:");
            	eliminaButton.setGraphic(emojiTextFlow2);

            	eliminaButton.getStyleClass().add("table-button-emoji");
                eliminaButton.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    confermaEliminazione(menu.getNome());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(eliminaButton);
                }
            }
        });

        // Aggiungere le colonne alla TableView
        tableMenu.getColumns().addAll(colNomeMenu, colModifica, colElimina);

        // Caricamento dei dati nella tabella
        try {
            List<Menu> menuList = MenuDAO.getInstance().getMenuByRistorante(ristorante);
            tableMenu.getItems().setAll(menuList);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore di connessione al database.");
        }

        container.getChildren().add(tableMenu);

        Button inserisciMenuButton = new Button("Inserisci Menu");
        inserisciMenuButton.getStyleClass().add("table-button");
        inserisciMenuButton.setOnAction(e -> switchToInserisciMenu());
        container.getChildren().add(inserisciMenuButton);
    }

    private void loadOrdini() {
        // Qui non vogliamo sovrascrivere i dati del menu, quindi non facciamo container.getChildren().clear();
        Label ordiniLabel = new Label("Ordini");
        ordiniLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        container.getChildren().add(ordiniLabel);

        // Creazione della TableView per gli ordini (quindi 'table' diventa variabile di istanza)
        tableOrdini = new TableView<>();
        tableOrdini.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Creazione delle colonne per la TableView
        TableColumn<Ordine, String> colOrdine = new TableColumn<>("Ordine");
        colOrdine.setCellValueFactory(data -> new SimpleStringProperty("Ordine " + data.getValue().getIdOrdine()));

        TableColumn<Ordine, Double> colPrezzo = new TableColumn<>("Prezzo");
        colPrezzo.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

        // Aggiungi la colonna Ristorante
        TableColumn<Ordine, String> colRistorante = new TableColumn<>("Ristorante");
        colRistorante.setCellValueFactory(data -> new SimpleStringProperty(getNomeRistoranteById(data.getValue().getIdRistorante())));
        
        TableColumn<Ordine, String> colEmail = new TableColumn<>("Email cliente");
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));
        
        TableColumn<Ordine, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> {
            // Usa il metodo getFormattedDataOraOrdine per ottenere la data formattata
            String formattedDate = data.getValue().getFormattedDataOraOrdine();
            return new SimpleStringProperty(formattedDate);
        });

        TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
        colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
        	
        	private final Button accettaButton = new Button();
            private final Button rifiutaButton = new Button();

            private final EmojiTextFlow emojiTextFlowAcc = new EmojiTextFlow();
            private final EmojiTextFlow emojiTextFlowDel = new EmojiTextFlow();
            {            	
            	emojiTextFlowAcc.parseAndAppend(":white_check_mark:");
            	accettaButton.setGraphic(emojiTextFlowAcc);
            	
            	emojiTextFlowDel.parseAndAppend(":x:");
            	rifiutaButton.setGraphic(emojiTextFlowDel);

            	accettaButton.getStyleClass().add("table-button-emoji");
            	rifiutaButton.getStyleClass().add("table-button-emoji");
            	
                accettaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    accettaOrdine(ordine);  
                });

                rifiutaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    rifiutaOrdine(ordine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !getTableView().getItems().get(getIndex()).getStato().equals(StatoOrdine.PENDENTE)) {
                    setGraphic(null);
                } else {
                    HBox buttonBox = new HBox(10);
                    buttonBox.getChildren().addAll(accettaButton, rifiutaButton);
                    setGraphic(buttonBox);

                }
            }
        });

        tableOrdini.getColumns().addAll(colOrdine, colPrezzo, colStato, colRistorante, colEmail, colData, colAzione);

        // Caricamento dei dati
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);
        tableOrdini.getItems().setAll(ordiniList);

        container.getChildren().add(tableOrdini);
    }

    public void accettaOrdine(Ordine ordine) {
        OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.ACCETTATO.name());
        tableOrdini.getItems().remove(ordine);
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);
        tableOrdini.getItems().setAll(ordiniList);

        Utilities.showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
    }

    public void rifiutaOrdine(Ordine ordine) {
        OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.RIFIUTATO.name());
        tableOrdini.getItems().remove(ordine);
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);
        tableOrdini.getItems().setAll(ordiniList);

        Utilities.showAlert("Successo", "Hai rifiutato l'ordine " + ordine.getIdOrdine());
    }

    private void confermaEliminazione(String nomeMenu) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma eliminazione");
        alert.setHeaderText("Stai per eliminare un menu");
        alert.setContentText("Sei sicuro?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminaMenu(nomeMenu);
            }
        });
    }

    private void eliminaMenu(String nomeMenu) {
        try (Connection conn = DatabaseConnection.connect()) {
            MenuDAO.getInstance().rimuoviMenu(nomeMenu, ristorante);
            Utilities.showAlert("Successo", "Menu eliminato con successo.");
            container.getChildren().clear();
            loadMenu();
            loadOrdini();
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del menu.");
        }
    }

    private void switchToModificaMenu(String nomeMenu) throws SQLException {
        SessioneMenu.setNome(nomeMenu);
        PiattiTitolare piattiMenuScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiMenuScreen);
    }

    private void switchToInserisciMenu() {
        InserisciMenu inserisciMenuScreen = new InserisciMenu();
        this.getScene().setRoot(inserisciMenuScreen);
    }

    private void switchToMainScreenTitolare() {
        MainScreenTitolare MainScreenTitolareScreen = new MainScreenTitolare();
        this.getScene().setRoot(MainScreenTitolareScreen);
    }

    private String getNomeRistoranteById(int idRistorante) {
        try (Connection connection = DatabaseConnection.connect()) {
            String query = "SELECT nome FROM ristorante WHERE idRistorante = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, idRistorante);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nome");
                } else {
                    return "Ristorante non trovato";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Errore";
        }
    }
}