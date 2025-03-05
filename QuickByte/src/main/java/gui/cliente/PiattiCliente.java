package gui.cliente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sessione.*;
import dao.PiattoDAO;
import gui.main.Utilities;
import model.Piatto;
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
        setupTableView();
        loadPiatti();
        
        
        Button btnIndietro = new Button("Indietro");
        btnIndietro.setOnAction(event -> tornaIndietro());

        this.getChildren().add(btnIndietro);
    }

    private void setupTableView() {
        tableView = new TableView<>();

        TableColumn<Piatto, String> nomeCol = new TableColumn<>("Piatto");
        nomeCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        TableColumn<Piatto, Double> costoCol = new TableColumn<>("Costo (€)");
        costoCol.setCellValueFactory(new PropertyValueFactory<>("prezzo"));

        TableColumn<Piatto, Void> descrizioneCol = new TableColumn<>("Descrizione");
        descrizioneCol.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button btnDesc = new Button("Vedi descrizione");

            {
                btnDesc.setOnAction(event -> {
                    Piatto piatto = getTableRow().getItem(); // Recupera il piatto della riga corrente
                    if (piatto != null) {
                        SessionePiatto.setId(piatto.getIdPiatto());  // Salva l'ID del piatto nella sessione
                        try {
                            switchToPiattoCliente();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDesc);
                }
            }
        });


        TableColumn<Piatto, Void> aggiungiCol = new TableColumn<>("");
        aggiungiCol.setCellFactory(data -> new TableCell<Piatto, Void>() {
            private final Button btnCart = new Button("Aggiungi al carrello");
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

        tableView.getColumns().addAll(nomeCol, costoCol, descrizioneCol, aggiungiCol);
        this.getChildren().add(tableView);
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
    
    private void tornaIndietro() {
        MenuCliente menuClienteScreen = new MenuCliente();
        this.getScene().setRoot(menuClienteScreen);
    }
}
