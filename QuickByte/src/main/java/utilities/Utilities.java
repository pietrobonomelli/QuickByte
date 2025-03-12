package utilities;

import com.pavlobu.emojitextflow.EmojiTextFlow;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    
    public static Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    public static Button createButtonEmoji(String text, String emoji, Runnable action) {
        Button button = new Button(text);
        EmojiTextFlow emojiTextFlow = new EmojiTextFlow();
        emojiTextFlow.parseAndAppend(emoji);
        button.setGraphic(emojiTextFlow);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("table-button-emoji");
        return button;
    }

    public static Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("button");
        return button;
    }

    public static Button createButtonLogout(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        button.getStyleClass().add("button-logout");
        return button;
    }
}
