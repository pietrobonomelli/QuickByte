package gui.cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sessione.*;
import dao.MenuDAO;
import dao.PiattoDAO;
import gui.main.Utilities;
import model.Piatto;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.sql.*;
import java.util.List;

public class PiattiCliente extends VBox {

    private String emailCliente;
    private String nomeMenu;
    private int idRistorante;
    private TableView<Piatto> tableView;

    public PiattiCliente() throws SQLException {
        super(10);

        this.idRistorante = SessioneRistorante.getId();
        this.nomeMenu = SessioneMenu.getNome();
        this.emailCliente = SessioneUtente.getEmail();

        this.setStyle("-fx-padding: 10;");
        
        // Crea un titolo da visualizzare sopra la tabella
        Label titleLabel = new Label("Piatti del menu: " + nomeMenu);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Aggiungi il titolo alla scena sopra la tabella
        setupTableView();
        loadPiatti();
        
        // Bottone per tornare indietro
        Button btnIndietro = new Button("⬅ INDIETRO");
        btnIndietro.setOnAction(event -> tornaIndietro());

        // Aggiungi il titolo, la tabella e il bottone alla scena
        this.getChildren().addAll(titleLabel, tableView, btnIndietro);  // titleLabel prima della tabella
    }

    private void setupTableView() {
        tableView = new TableView<>();

        // Colonna Nome
        TableColumn<Piatto, String> nomeCol = new TableColumn<>("Piatto");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        // Colonna Costo
        TableColumn<Piatto, Double> costoCol = new TableColumn<>("Costo (€)");
        costoCol.setCellValueFactory(new PropertyValueFactory<>("prezzo"));

        // Colonna Allergeni
        TableColumn<Piatto, String> allergeniCol = new TableColumn<>("Allergeni");
        allergeniCol.setCellValueFactory(new PropertyValueFactory<>("allergeni"));

        // Colonna Vedi Foto
        TableColumn<Piatto, Void> vediFotoCol = new TableColumn<>("Vedi Foto");
        vediFotoCol.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button btnFoto = new Button("VEDI FOTO");

            {
                btnFoto.setOnAction(event -> {
                    Piatto piatto = getTableRow().getItem(); // Recupera il piatto della riga corrente
                    if (piatto != null) {
                        // Visualizza la foto in una finestra di dialogo
                        showPhotoDialog(piatto.getFoto());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnFoto);
                }
            }
        });

        // Colonna Aggiungi al carrello
        TableColumn<Piatto, Void> aggiungiCol = new TableColumn<>("");
        aggiungiCol.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button btnCart = new Button("AGGIUNGI AL CARRELLO 🛒");

            {
                btnCart.setOnAction(event -> {
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
                    setGraphic(btnCart);
                }
            }
        });

        // Aggiungi tutte le colonne alla TableView
        tableView.getColumns().addAll(nomeCol, costoCol, allergeniCol, vediFotoCol, aggiungiCol);
    }

    private void loadPiatti() {
        try {
            List<Piatto> piatti = PiattoDAO.getInstance().getPiattiByMenuAndIdRistorante(nomeMenu, idRistorante);
            ObservableList<Piatto> piattiList = FXCollections.observableArrayList(piatti);
            tableView.setItems(piattiList);
        } catch (SQLException e) {
            e.printStackTrace();
            Utilities.showAlert("Errore", "Errore nel caricamento dei piatti.");
        }
    }
    
    private void switchToPiattoCliente() throws SQLException {
        PiattoCliente piattoClienteScreen = new PiattoCliente();
        this.getScene().setRoot(piattoClienteScreen);
    }

    private void aggiungiAlCarrello(int idPiatto) throws SQLException {
        boolean pieno = SessioneCarrello.getPieno();
        int idRistoranteCarrello = SessioneCarrello.getIdRistorante();

        if (!pieno) {
            SessioneCarrello.setIdRistorante(idRistorante);
            SessioneCarrello.setPieno(true);
            System.out.println("SETTO PIENO TRUE");
            PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
        } else if (idRistoranteCarrello == idRistorante) {
            PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
        } else {
            System.out.println("TERZO ELSE");
            mostraPopupConferma(idPiatto);
        }
    }

    private void mostraPopupConferma(int idPiatto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attenzione");
        alert.setHeaderText("Hai già piatti di un altro ristorante nel carrello.");
        alert.setContentText("Vuoi svuotare il carrello e procedere con il nuovo ristorante?");

        ButtonType btnProcedi = new ButtonType("Procedi");
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnProcedi, btnAnnulla);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnProcedi) {
            	try {
					PiattoDAO.getInstance().svuotaCarrello(emailCliente);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println("SVUOTO IL CARRELLO DI " + emailCliente);
                SessioneCarrello.setPieno(true);
                SessioneCarrello.setIdRistorante(idRistorante);
                
                try {
                    PiattoDAO.getInstance().aggiungiPiattoAlCarrello(idPiatto, emailCliente);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showPhotoDialog(String fotoUrl) {
        // Finestra di dialogo per visualizzare la foto
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            ImageView imageView = new ImageView(new Image(fotoUrl));
            imageView.setFitWidth(300);
            imageView.setFitHeight(200);
            StackPane stackPane = new StackPane(imageView);
            Scene photoScene = new Scene(stackPane, 400, 300);

            Stage photoStage = new Stage();
            photoStage.setTitle("Foto del piatto");
            photoStage.setScene(photoScene);
            photoStage.show();
        } else {
            Utilities.showAlert("Errore", "Foto non disponibile per questo piatto.");
        }
    }

    private void tornaIndietro() {
        MenuCliente menuClienteScreen = new MenuCliente();
        this.getScene().setRoot(menuClienteScreen);
    }
}
