package gui.corriere;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.LoginDAO;
import dao.OrdineDAO;
import model.Ordine;
import model.StatoOrdine;
import sessione.SessioneUtente;
import gui.main.LoginScreen;
import gui.main.Utilities;
import java.util.List;

public class MainScreenCorriere extends VBox {

	private TableView<Ordine> table;

	public MainScreenCorriere() {
		super(10);
		this.setStyle("-fx-padding: 10;");

		Label titleLabel = new Label("Ordini Disponibili");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		table = new TableView<>();
		table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		TableColumn<Ordine, Integer> colId = new TableColumn<>("ID Ordine");
		colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getIdOrdine()).asObject());

		TableColumn<Ordine, Double> colCosto = new TableColumn<>("Costo (€)");
		colCosto.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

		TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
		colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato()));

		TableColumn<Ordine, String> colEmail = new TableColumn<>("Email cliente");
		colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

		TableColumn<Ordine, String> colData = new TableColumn<>("Data");
		colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDataOraOrdine()));

		TableColumn<Ordine, String> colIndirizzo = new TableColumn<>("Indirizzo");
		colIndirizzo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIndirizzo()));


		TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
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
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(accettaButton);
				}
			}
		});


		table.getColumns().addAll(colId, colCosto, colStato, colEmail, colIndirizzo, colData, colAzione);
		loadOrdini();

		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(e -> switchToLoginScreen());
		logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");

		this.getChildren().addAll(titleLabel, table, logoutButton);
	}

	private void loadOrdini() {
		List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByStato(StatoOrdine.ACCETTATO.name());
		ObservableList<Ordine> ordini = FXCollections.observableArrayList(ordiniList);
		table.setItems(ordini);
	}
	
	private void accettaOrdine(Ordine ordine) {
	    String emailCorriere = SessioneUtente.getEmail(); // Recupera l'email del corriere dalla sessione

	    if (emailCorriere == null || emailCorriere.isEmpty()) {
	        Utilities.showAlert("Errore", "Errore nel recupero dell'email del corriere.");
	        return;
	    }

	    boolean statoAggiornato = OrdineDAO.getInstance().aggiornaStatoOrdine(ordine.getIdOrdine(), StatoOrdine.IN_CONSEGNA.name());
	    boolean emailAggiornata = OrdineDAO.getInstance().aggiornaEmailCorriereOrdine(ordine.getIdOrdine(), emailCorriere);

	    if (statoAggiornato && emailAggiornata) {
	        Utilities.showAlert("Successo", "Hai accettato l'ordine " + ordine.getIdOrdine());
	        loadOrdini(); // Aggiorna la tabella
	    } else {
	        Utilities.showAlert("Errore", "Non è stato possibile accettare l'ordine. Riprova.");
	    }
	}


	private void switchToLoginScreen() {
		LoginScreen loginScreen = new LoginScreen();
		this.getScene().setRoot(loginScreen);
	}
}