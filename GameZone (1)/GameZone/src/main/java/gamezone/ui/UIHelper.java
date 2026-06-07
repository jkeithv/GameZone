package gamezone.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIHelper {

    public static final String BG_DARK    = "#0f0f1a";
    public static final String BG_CARD    = "#1a1a2e";
    public static final String ACCENT     = "#c850c0";
    public static final String ACCENT2    = "#7b2d8b";
    public static final String TEXT_LIGHT = "#eeeeee";
    public static final String TEXT_DIM   = "#888899";

    public static Label title(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(ACCENT));
        return lbl;
    }

    public static Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(TEXT_DIM));
        return lbl;
    }

    public static Label bodyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 13));
        lbl.setTextFill(Color.web(TEXT_LIGHT));
        return lbl;
    }

    public static TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #0f0f1a; -fx-text-fill: #eeeeee;" +
            "-fx-border-color: #333355; -fx-border-radius: 6; -fx-background-radius: 6;" +
            "-fx-prompt-text-fill: #555577; -fx-padding: 8;"
        );
        tf.setPrefHeight(38);
        return tf;
    }

    public static ComboBox<String> styledCombo(String... options) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(options);
        cb.setStyle(
            "-fx-background-color: #0f0f1a; -fx-text-fill: #eeeeee;" +
            "-fx-border-color: #333355; -fx-border-radius: 6; -fx-background-radius: 6;"
        );
        cb.setPrefHeight(38);
        return cb;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: #c850c0; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 13;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #a03090; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 13;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #c850c0; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 13;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
        ));
        return btn;
    }

    public static Button dangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: #aa2222; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-font-size: 13;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
        );
        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: #2a2a4a; -fx-text-fill: #ccccdd;" +
            "-fx-font-size: 13; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 22 10 22;"
        );
        return btn;
    }

    public static VBox card(javafx.scene.Node... children) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: #1a1a2e; -fx-background-radius: 12;" +
            "-fx-border-color: #2a2a4a; -fx-border-radius: 12; -fx-border-width: 1;"
        );
        card.getChildren().addAll(children);
        return card;
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dp = alert.getDialogPane();
        dp.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #7b2d8b;");
        dp.lookup(".content.label").setStyle("-fx-text-fill: #eeeeee; -fx-font-size: 13;");
        alert.showAndWait();
    }

    public static void showError(String message) {
        showAlert("❌ Error", message, Alert.AlertType.ERROR);
    }

    public static void showSuccess(String message) {
        showAlert("✅ Éxito", message, Alert.AlertType.INFORMATION);
    }

    public static void showWarning(String message) {
        showAlert("⚠️ Advertencia", message, Alert.AlertType.WARNING);
    }
}
