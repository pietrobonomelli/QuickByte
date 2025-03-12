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
	private TableView<Ordine> tableOrdini;

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

		Button tornaButton = Utilities.createButton("⬅ INDIETRO", this::switchToMainScreenTitolare);
		buttonContainer.getChildren().add(tornaButton);

		this.getChildren().add(buttonContainer);

		loadOrdini();
	}

	private void loadMenu() {
		Label titleLabel = Utilities.createLabel("Menu", "title");
		container.getChildren().add(titleLabel);

		// Creazione della TableView per il menu
		TableView<Menu> tableMenu = new TableView<>();
		tableMenu.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableMenu.getStyleClass().add("table-view");

		// Creazione delle colonne per la TableView
		TableColumn<Menu, String> colNomeMenu = new TableColumn<>("Nome Menu");
		colNomeMenu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));

		TableColumn<Menu, Void> colModifica = createButtonColumn(":pencil:", this::switchToModificaMenu);
		TableColumn<Menu, Void> colElimina = createButtonColumn(":wastebasket:", this::confermaEliminazione);

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

		Button inserisciMenuButton = Utilities.createButton("Inserisci Menu", this::switchToInserisciMenu);
		container.getChildren().add(inserisciMenuButton);
	}

	private TableColumn<Menu, Void> createButtonColumn(String emoji, ActionHandler<Menu> actionHandler) {
		TableColumn<Menu, Void> col = new TableColumn<>("");
		col.setCellFactory(param -> new TableCell<Menu, Void>() {
			private final Button button = new Button();
			private final EmojiTextFlow emojiTextFlow = new EmojiTextFlow();

			{
				emojiTextFlow.parseAndAppend(emoji);
				button.setGraphic(emojiTextFlow);
				button.getStyleClass().add("table-button-emoji");
				button.setOnAction(event -> {
					Menu menu = getTableView().getItems().get(getIndex());
					try {
						actionHandler.handle(menu);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : button);
			}
		});
		return col;
	}


	private void loadOrdini() {
		Label ordiniLabel = Utilities.createLabel("Ordini", "ordini-label");
		ordiniLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
		container.getChildren().add(ordiniLabel);

		// Creazione della TableView per gli ordini
		tableOrdini = new TableView<>();
		tableOrdini.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

		// Creazione delle colonne per la TableView
		TableColumn<Ordine, String> colOrdine = new TableColumn<>("Ordine");
		colOrdine.setCellValueFactory(data -> new SimpleStringProperty("Ordine " + data.getValue().getIdOrdine()));

		TableColumn<Ordine, Double> colPrezzo = new TableColumn<>("Prezzo");
		colPrezzo.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getCosto()).asObject());

		TableColumn<Ordine, String> colStato = new TableColumn<>("Stato");
		colStato.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStato().name()));

		TableColumn<Ordine, String> colRistorante = new TableColumn<>("Ristorante");
		colRistorante.setCellValueFactory(data -> new SimpleStringProperty(getNomeRistoranteById(data.getValue().getIdRistorante())));

		TableColumn<Ordine, String> colEmail = new TableColumn<>("Email cliente");
		colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmailCliente()));

		TableColumn<Ordine, String> colData = new TableColumn<>("Data");
		colData.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedDataOraOrdine()));

		TableColumn<Ordine, Void> colAzione = createOrderActionColumn();

		tableOrdini.getColumns().addAll(colOrdine, colPrezzo, colStato, colRistorante, colEmail, colData, colAzione);

		// Caricamento dei dati
		List<Ordine> ordiniList = OrdineDAO.getInstance().getOrdiniByIdRistorante(ristorante);
		tableOrdini.getItems().setAll(ordiniList);

		container.getChildren().add(tableOrdini);
	}

	private TableColumn<Ordine, Void> createOrderActionColumn() {
		TableColumn<Ordine, Void> colAzione = new TableColumn<>("Azione");
		colAzione.setCellFactory(param -> new TableCell<Ordine, Void>() {
			private final Button accettaButton = Utilities.createButtonEmoji("", ":white_check_mark:", () -> accettaOrdine(getTableView().getItems().get(getIndex())));
			private final Button rifiutaButton = Utilities.createButtonEmoji("", ":x:", () -> rifiutaOrdine(getTableView().getItems().get(getIndex())));

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
		return colAzione;
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

	private void confermaEliminazione(Menu menu) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Conferma eliminazione");
		alert.setHeaderText("Stai per eliminare un menu");
		alert.setContentText("Sei sicuro?");

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				eliminaMenu(menu.getNome());
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

	private void switchToModificaMenu(Menu menu) throws SQLException {
		SessioneMenu.setNome(menu.getNome());
		PiattiTitolare piattiMenuScreen = new PiattiTitolare();
		this.getScene().setRoot(piattiMenuScreen);
	}

	private void switchToInserisciMenu() {
		InserisciMenu inserisciMenuScreen = new InserisciMenu();
		this.getScene().setRoot(inserisciMenuScreen);
	}

	private void switchToMainScreenTitolare() {
		MainScreenTitolare mainScreenTitolareScreen = new MainScreenTitolare();
		this.getScene().setRoot(mainScreenTitolareScreen);
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

	@FunctionalInterface
	private interface ActionHandler<T> {
		void handle(T item) throws SQLException;
	}
}
