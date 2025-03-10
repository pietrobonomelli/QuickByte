package utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Utilities {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static VBox createFieldBox(String labelText, String promptText, TextField field) {
        VBox fieldBox = new VBox();
        Text label = new Text(labelText);
        label.getStyleClass().add("label");
        field.setPromptText(promptText);
        field.getStyleClass().add("text-field");
        field.setMaxWidth(280);
        fieldBox.getChildren().addAll(label, field);
        fieldBox.getStyleClass().add("field-box");
        return fieldBox;
    }
}
