package gamezone.ui;

import gamezone.model.*;
import gamezone.service.VideoGameService;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class SearchByPlatformPanel extends VBox {

    private final VideoGameService service;
    private TableView<VideoGame> table;
    private Label resultLabel;

    public SearchByPlatformPanel(VideoGameService service) {
        this.service = service;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #0f0f1a;");
        getChildren().addAll(UIHelper.title("🕹️ Buscar por Plataforma"), buildSearch(), buildTable());
    }

    private VBox buildSearch() {
        VBox card = UIHelper.card();
        Label lbl = UIHelper.sectionLabel("Ingresa la plataforma:");

        TextField field = UIHelper.styledField("Ej: PC, PS5, Xbox, Nintendo Switch");
        field.setPrefWidth(350);

        Button btn = UIHelper.primaryButton("🔍 Buscar");
        btn.setOnAction(e -> search(field.getText().trim()));
        field.setOnAction(e -> search(field.getText().trim()));

        HBox row = new HBox(12, field, btn);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(lbl, row);
        return card;
    }

    private VBox buildTable() {
        VBox card = UIHelper.card();
        resultLabel = UIHelper.bodyLabel("Ingresa una plataforma para buscar.");
        resultLabel.setTextFill(Color.web("#888899"));

        table = new TableView<>();
        table.setPrefHeight(380);
        table.setVisible(false);

        TableColumn<VideoGame, String> colTitle    = strCol("Título", "title", 200);
        TableColumn<VideoGame, String> colType     = new TableColumn<>("Tipo");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue() instanceof DigitalVideoGame ? "Digital" : "Físico"));
        colType.setPrefWidth(80);

        TableColumn<VideoGame, String> colGenre    = strCol("Género", "genre", 110);
        TableColumn<VideoGame, Number> colPrice    = new TableColumn<>("Precio Final");
        colPrice.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().calculateFinalPrice()));
        colPrice.setCellFactory(c -> new TableCell<VideoGame, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("$%.0f", v.doubleValue()));
            }
        });
        colPrice.setPrefWidth(110);

        TableColumn<VideoGame, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getStock()));
        colStock.setPrefWidth(70);

        table.setStyle(
            "-fx-background-color: #0f0f1a; -fx-control-inner-background: #0f0f1a;" +
            "-fx-table-cell-border-color: #1a1a2e; -fx-text-fill: #eeeeee;"
        );
        table.getColumns().addAll(colTitle, colType, colGenre, colPrice, colStock);

        card.getChildren().addAll(resultLabel, table);
        return card;
    }

    private void search(String platform) {
        if (platform.isBlank()) {
            UIHelper.showWarning("Por favor ingresa una plataforma.");
            return;
        }
        List<VideoGame> results = service.findByPlatform(platform);
        if (results == null || results.isEmpty()) {
            resultLabel.setText("❌ No se encontraron juegos para la plataforma: \"" + platform + "\"");
            resultLabel.setTextFill(Color.web("#ff6666"));
            table.setVisible(false);
        } else {
            resultLabel.setText("✅ Se encontraron " + results.size() + " juego(s) para: \"" + platform + "\"");
            resultLabel.setTextFill(Color.web("#55ff99"));
            table.getItems().setAll(results);
            table.setVisible(true);
        }
    }

    private TableColumn<VideoGame, String> strCol(String title, String prop, int width) {
        TableColumn<VideoGame, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }
}
