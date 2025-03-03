package gui.titolare;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.*;
import database.DatabaseConnection;
import model.*;
import model.Menu;
import sessione.SessioneMenu;
import sessione.SessioneRistorante;

import java.sql.*;
import java.util.List;

public class MenuTitolare extends VBox {

    private VBox container;
    private int ristorante;

    public MenuTitolare() {
        super(10);
        this.ristorante = SessioneRistorante.getId();
        System.out.println("Ristorante passato a GestisciMenu: " + ristorante);  // Debug
        this.setStyle("-fx-padding: 10;");
        container = new VBox(10);
        loadMenu();
        this.getChildren().add(container);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-padding: 10;");

        Button tornaButton = new Button("Torna a gestione ristoranti");
        tornaButton.setOnAction(e -> swirchToMainScreenTitolare());
        buttonContainer.getChildren().add(tornaButton);

        Button inserisciMenuButton = new Button("Inserisci Menu");
        inserisciMenuButton.setOnAction(e -> switchToInserisciMenu());
        buttonContainer.getChildren().add(inserisciMenuButton);

        this.getChildren().add(buttonContainer);
        
        
        loadOrdini();
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            List<Menu> menuList = MenuDAO.getInstance().getMenuByRistorante(ristorante);
            
            for (Menu menu : menuList) {
                String nomeMenu = menu.getNome();
                HBox menuBox = new HBox(10);
                menuBox.setStyle("-fx-padding: 10;");
                Label nomeMenuLabel = new Label(nomeMenu);

                nomeMenuLabel.setOnMouseClicked(e -> {
					try {
						switchToPiattiTitolare(nomeMenu);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});

                MenuButton optionsButton = new MenuButton("⋮");
                MenuItem modificaItem = new MenuItem("Modifica");
                modificaItem.setOnAction(e -> {
					try {
						switchToModificaMenu(nomeMenu);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				});

                MenuItem eliminaItem = new MenuItem("Elimina");
                eliminaItem.setOnAction(e -> confermaEliminazione(nomeMenu));

                optionsButton.getItems().addAll(modificaItem, eliminaItem);

                menuBox.getChildren().addAll(nomeMenuLabel, optionsButton);
                container.getChildren().add(menuBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }
    
    
    private void loadOrdini() {
        TableView<Ordine> table = new TableView<>();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        // Creazione delle colonne
        TableColumn<Ordine, String> colOrdine = new TableColumn<>("Ordine");
        colOrdine.setCellValueFactory(data -> new SimpleStringProperty("Ordine " + data.getValue().getIdOrdine()));

        TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

        TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

        TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
        colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato()));

        TableColumn<Ordine, Void> colAzione = new TableColumn<>("");
        colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
            private final Button accettaButton = new Button("Accetta");

            {
                accettaButton.setOnAction(event -> {
                    Ordine ordine = getTableView().getItems().get(getIndex());
                    accettaOrdine(ordine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !getTableView().getItems().get(getIndex()).getStato().equals(StatoOrdine.PENDENTE.name())) {
                    setGraphic(null);
                } else {
                    setGraphic(accettaButton);
                }
            }
        });


        table.getColumns().addAll(colOrdine, colId, colCosto, colStato, colAzione);

        // Caricamento dei dati
        List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);
        table.getItems().setAll(ordiniList);

        // Sostituisci il VBox con la TableView
        this.getChildren().clear();
        this.getChildren().add(table);
    }
    
    public void accettaOrdine(Ordine ordine) {
        OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.ACCETTATO.name());
        showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
        loadOrdini();  // Ricarica solo gli ordini
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
            showAlert("Successo", "Menu eliminato con successo.");
            container.getChildren().clear();
            loadMenu();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del menu.");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
