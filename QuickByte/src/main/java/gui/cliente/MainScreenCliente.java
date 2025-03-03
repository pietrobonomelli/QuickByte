package gui.cliente;

import javafx.scene.control.*;
import gui.main.*;
import javafx.scene.layout.*;
import sessione.SessioneRistorante;
import javafx.scene.input.MouseButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import dao.RistoranteDAO;
import model.Ristorante;
import java.sql.SQLException;
import java.util.List;

public class MainScreenCliente extends VBox {

	private VBox container;

	public MainScreenCliente() {
		super(10);
		this.setStyle("-fx-padding: 10;");
		container = new VBox(10);
		this.getChildren().add(container);
		loadRistoranti();

		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(e -> switchToLoginScreen());
		logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
		this.getChildren().add(logoutButton);
	}

	private void loadRistoranti() {
		try {
			List<Ristorante> ristoranti = RistoranteDAO.getInstance().getRistoranti();
			ObservableList<String> ristorantiNames = FXCollections.observableArrayList();
			for (Ristorante ristorante : ristoranti) {
				ristorantiNames.add(ristorante.getNome());

				// Aggiungi il ristorante all'interfaccia grafica
				HBox ristoranteBox = new HBox(10);
				ristoranteBox.setStyle("-fx-padding: 10;");
				Label nomeRistorante = new Label(ristorante.getNome());

				nomeRistorante.setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.PRIMARY) {
						// Salva l'ID del ristorante in SessioneRistorante
						SessioneRistorante.setId(ristorante.getIdRistorante());
						switchToMenuCliente();
					}
				});

				ristoranteBox.getChildren().addAll(nomeRistorante);
				container.getChildren().add(ristoranteBox);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Errore", "Errore di connessione al database.");
		}
	}

	private void switchToMenuCliente() {
		MenuCliente menuClienteScreen = new MenuCliente(); // La schermata MenuCliente prende l'ID del ristorante dalla sessione
		this.getScene().setRoot(menuClienteScreen);
	}

	private void switchToLoginScreen() {
		LoginScreen loginScreen = new LoginScreen();
		this.getScene().setRoot(loginScreen);
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
