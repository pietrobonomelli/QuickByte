package gui.titolare;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.*;
import database.DatabaseConnection;
import sessione.SessioneRistorante;
import utilities.Utilities;
import sessione.SessioneMenu;
import model.*;
import model.Menu;
import java.sql.*;
import java.util.List;

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

        Button tornaButton = new Button("Torna a gestione ristoranti");
        tornaButton.setOnAction(e -> swirchToMainScreenTitolare());
        buttonContainer.getChildren().add(tornaButton);

        this.getChildren().add(buttonContainer);

        loadOrdini();
    }

    private void loadMenu() {
        Label titleLabel = new Label("Menu");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        container.getChildren().add(titleLabel);

        // Creazione della TableView per il menu
        TableView<Menu> tableMenu = new TableView<>();
        tableMenu.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Creazione delle colonne per la TableView
        TableColumn<Menu, String> colNomeMenu = new TableColumn<>("Nome Menu");
        colNomeMenu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

        TableColumn<Menu, Void> colModifica = new TableColumn<>("Modifica");
        colModifica.setCellFactory(param -> new TableCell<Menu, Void>() {
            private final Button modificaButton = new Button("Modifica");

            {
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
            private final Button eliminaButton = new Button("Elimina");

            {
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

        // Aggiungere la TableView alla schermata
        container.getChildren().add(tableMenu);

        // Bottone per inserire un nuovo menu
        Button inserisciMenuButton = new Button("Inserisci Menu");
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
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato()));

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
            private final Button accettaButton = new Button("Accetta");
            private final Button rifiutaButton = new Button("Rifiuta");
            {
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
                if (empty || !getTableView().getItems().get(getIndex()).getStato().equals(StatoOrdine.PENDENTE.name())) {
                    setGraphic(null);
                } else {
                    VBox buttonBox = new VBox(5);
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
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore durante l'eliminazione del menu.");
        }
    }

    private void switchToPiattiTitolare(String nomeMenu) throws SQLException {
        SessioneMenu.setNome(nomeMenu);
        PiattiTitolare piattiTitolareScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiTitolareScreen);
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

    private void swirchToMainScreenTitolare() {
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
