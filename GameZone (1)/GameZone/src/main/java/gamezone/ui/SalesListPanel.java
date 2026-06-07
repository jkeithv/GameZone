package gamezone.ui;

import gamezone.model.Sale;
import gamezone.service.VideoGameService;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesListPanel extends VBox {

    private final VideoGameService service;
    private TableView<Sale> table;
    private Label statsLabel;

    public SalesListPanel(VideoGameService service) {
        this.service = service;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #0f0f1a;");
        getChildren().addAll(UIHelper.title("📊 Historial de Ventas"), buildStats(), buildTable());
        loadTable();
    }

    private HBox buildStats() {
        HBox row = new HBox(20);
        statsLabel = UIHelper.bodyLabel("Cargando estadísticas...");
        row.getChildren().add(statsLabel);
        return row;
    }

    private VBox buildTable() {
        VBox card = UIHelper.card();

        table = new TableView<>();
        table.setPrefHeight(480);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        TableColumn<Sale, String> colId   = new TableColumn<>("ID Venta");
        colId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));
        colId.setPrefWidth(100);

        TableColumn<Sale, String> colGame = new TableColumn<>("Videojuego");
        colGame.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVideoGame().getTitle()));
        colGame.setPrefWidth(220);

        TableColumn<Sale, String> colPlatform = new TableColumn<>("Plataforma");
        colPlatform.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getVideoGame().getPlatform()));
        colPlatform.setPrefWidth(110);

        TableColumn<Sale, Number> colQty = new TableColumn<>("Cantidad");
        colQty.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getQuantity()));
        colQty.setPrefWidth(80);

        TableColumn<Sale, Number> colUnit = new TableColumn<>("Precio Unidad.");
        colUnit.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getUnitPrice()));
        colUnit.setCellFactory(c -> new TableCell<Sale, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("$%.0f", v.doubleValue()));
            }
        });
        colUnit.setPrefWidth(110);

        TableColumn<Sale, Number> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getTotal()));
        colTotal.setCellFactory(c -> new TableCell<Sale, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); return; }
                setText(String.format("$%.0f", v.doubleValue()));
                setTextFill(Color.web("#c850c0"));
                setFont(Font.font("Arial", FontWeight.BOLD, 13));
            }
        });
        colTotal.setPrefWidth(110);

        TableColumn<Sale, String> colDate = new TableColumn<>("Fecha");
        colDate.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getSaleDate().format(fmt)));
        colDate.setPrefWidth(140);

        table.setStyle(
            "-fx-background-color: #0f0f1a; -fx-control-inner-background: #0f0f1a;" +
            "-fx-table-cell-border-color: #1a1a2e; -fx-text-fill: #eeeeee;"
        );
        table.getColumns().addAll(colId, colGame, colPlatform, colQty, colUnit, colTotal, colDate);

        Button refreshBtn = UIHelper.secondaryButton("🔄 Actualizar");
        refreshBtn.setOnAction(e -> loadTable());

        card.getChildren().addAll(table, refreshBtn);
        return card;
    }

    private void loadTable() {
        List<Sale> sales = service.getAllSales();
        table.getItems().setAll(sales);

        double totalRevenue = sales.stream().mapToDouble(Sale::getTotal).sum();
        int totalUnits = sales.stream().mapToInt(Sale::getQuantity).sum();
        statsLabel.setText(String.format(
            "📦 Total ventas: %d   |   🎮 Unidades vendidas: %d   |   💰 Ingresos totales: $%.0f",
            sales.size(), totalUnits, totalRevenue
        ));
    }
}
