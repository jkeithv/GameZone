package gamezone.ui;

import gamezone.model.*;
import gamezone.service.VideoGameService;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ListGamesPanel extends VBox {

    private final VideoGameService service;
    private TableView<VideoGame> table;
    private Label countLabel;

    public ListGamesPanel(VideoGameService service) {
        this.service = service;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #0f0f1a;");
        getChildren().addAll(UIHelper.title("📋 Todos los Videojuegos"), buildTable());
        loadTable();
    }

    private VBox buildTable() {
        VBox card = UIHelper.card();
        countLabel = UIHelper.bodyLabel("Cargando...");

        table = new TableView<>();
        table.setPrefHeight(500);

        TableColumn<VideoGame, String> colTitle    = strCol("Título", "title", 200);
        TableColumn<VideoGame, String> colType     = new TableColumn<>("Tipo");
        colType.setCellValueFactory(cd -> new SimpleStringProperty(
            cd.getValue() instanceof DigitalVideoGame ? "🖥 Digital" : "📦 Físico"));
        colType.setPrefWidth(90);

        TableColumn<VideoGame, String> colPlatform = strCol("Plataforma", "platform", 110);
        TableColumn<VideoGame, String> colGenre    = strCol("Género", "genre", 110);

        TableColumn<VideoGame, Number> colBasePrice = new TableColumn<>("Precio Base");
        colBasePrice.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getPrice()));
        colBasePrice.setCellFactory(c -> new TableCell<VideoGame, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("$%.0f", v.doubleValue()));
            }
        });
        colBasePrice.setPrefWidth(110);

        TableColumn<VideoGame, Number> colFinalPrice = new TableColumn<>("Precio Final");
        colFinalPrice.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().calculateFinalPrice()));
        colFinalPrice.setCellFactory(c -> new TableCell<VideoGame, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("$%.0f", v.doubleValue()));
            }
        });
        colFinalPrice.setPrefWidth(110);

        TableColumn<VideoGame, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getStock()));
        colStock.setPrefWidth(70);
        colStock.setCellFactory(c -> new TableCell<VideoGame, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v.toString());
                setStyle(v.intValue() == 0 ? "-fx-text-fill: #ff5555;" :
                          v.intValue() < 5  ? "-fx-text-fill: #ffaa55;" :
                                              "-fx-text-fill: #55ff99;");
            }
        });

        TableColumn<VideoGame, String> colExtra = new TableColumn<>("Detalle");
        colExtra.setCellValueFactory(cd -> {
            VideoGame g = cd.getValue();
            if (g instanceof DigitalVideoGame dg)
                return new SimpleStringProperty(dg.getSizeGB() + "GB | " + dg.getDownloadPlatform());
            else if (g instanceof PhysicalVideoGame pg)
                return new SimpleStringProperty(pg.getCondition() + " | " + pg.getDistributor());
            return new SimpleStringProperty("-");
        });
        colExtra.setPrefWidth(200);

        styleTable(table);
        table.getColumns().addAll(colTitle, colType, colPlatform, colGenre, colBasePrice, colFinalPrice, colStock, colExtra);

        Button refreshBtn = UIHelper.secondaryButton("🔄 Actualizar");
        refreshBtn.setOnAction(e -> loadTable());

        card.getChildren().addAll(countLabel, table, refreshBtn);
        return card;
    }

    private void loadTable() {
        var games = service.getAllGames();
        table.getItems().setAll(games);
        long digital  = games.stream().filter(g -> g instanceof DigitalVideoGame).count();
        long physical = games.stream().filter(g -> g instanceof PhysicalVideoGame).count();
        countLabel.setText("Total: " + games.size() + " juegos  |  Digital: " + digital + "  |  Físico: " + physical);
    }

    private TableColumn<VideoGame, String> strCol(String title, String prop, int width) {
        TableColumn<VideoGame, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void styleTable(TableView<?> t) {
        t.setStyle(
            "-fx-background-color: #0f0f1a; -fx-control-inner-background: #0f0f1a;" +
            "-fx-table-cell-border-color: #1a1a2e; -fx-text-fill: #eeeeee;"
        );
    }
}
