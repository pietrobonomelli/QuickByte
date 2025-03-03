package gui.titolare;

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
        try (Connection conn = DatabaseConnection.connect()) {
            VBox ordiniContainer = new VBox(10);  // Creazione del container per gli ordini
            ordiniContainer.setStyle("-fx-padding: 10;");
            Label ordiniLabel = new Label("Ordini:");
            ordiniContainer.getChildren().add(ordiniLabel);

            List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);

            for (Ordine ordine : ordiniList) {
                HBox ordineBox = new HBox(10);
                ordineBox.setStyle("-fx-padding: 10;");
                Label ordineInfo = new Label("Ordine ID: " + ordine.getIdOrdine() + " - Costo: " + ordine.getCosto() + "€");
                Button accettaButton = new Button("Accetta");

                accettaButton.setOnAction(e -> accettaOrdine(ordine));

                ordineBox.getChildren().addAll(ordineInfo, accettaButton);
                ordiniContainer.getChildren().add(ordineBox);  // Aggiungi l'ordine al container
            }
            this.getChildren().add(ordiniContainer);  // Aggiungi ordiniContainer solo una volta alla scena
            System.out.println("OrdiniContainer aggiunto alla GUI");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore di connessione al database.");
        }
    }

    
    private void accettaOrdine(Ordine ordine) {
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
