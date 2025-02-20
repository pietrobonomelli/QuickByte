package gui.titolare;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import dao.MenuDAO;
import database.DatabaseConnection;
import gui.main.*;
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
    }

    private void loadMenu() {
        try (Connection conn = DatabaseConnection.connect()) {
            MenuDAO menuDAO = new MenuDAO(conn);
            List<Menu> menuList = menuDAO.getMenuByRistorante(ristorante);
            
            for (Menu menu : menuList) {
                String nomeMenu = menu.getNome();
                HBox menuBox = new HBox(10);
                menuBox.setStyle("-fx-padding: 10;");
                Label nomeMenuLabel = new Label(nomeMenu);

                // Aggiungi un evento di click sul nome del menu
                nomeMenuLabel.setOnMouseClicked(e -> switchToPiattiTitolare(nomeMenu));

                MenuButton optionsButton = new MenuButton("⋮");
                MenuItem modificaItem = new MenuItem("Modifica");
                modificaItem.setOnAction(e -> switchToModificaMenu(nomeMenu));

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
            MenuDAO menuDAO = new MenuDAO(conn);
            menuDAO.rimuoviMenu(nomeMenu, ristorante);
            showAlert("Successo", "Menu eliminato con successo.");
            container.getChildren().clear();
            loadMenu();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Errore", "Errore durante l'eliminazione del menu.");
        }
    }

    private void switchToPiattiTitolare(String nomeMenu) {
        // Salva il nome del menu nella sessione
        SessioneMenu.setNome(nomeMenu);

        // Passa alla pagina PiattiTitolare
        PiattiTitolare piattiTitolareScreen = new PiattiTitolare();
        this.getScene().setRoot(piattiTitolareScreen);
    }

    private void switchToModificaMenu(String nomeMenu) {
        // Imposta il nome del menu nella sessione
        SessioneMenu.setNome(nomeMenu);

        // Crea l'istanza di PiattiTitolare
        PiattiTitolare piattiMenuScreen = new PiattiTitolare();
        
        // Cambia la scena per visualizzare la pagina PiattiMenu
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
