package gamezone.ui;

import gamezone.model.*;
import gamezone.service.VideoGameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SalePanel extends VBox {

    private final VideoGameService service;
    private TextField titleField, qtyField;
    private VBox previewCard;

    public SalePanel(VideoGameService service) {
        this.service = service;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #0f0f1a;");
        previewCard = new VBox(10);
        getChildren().addAll(UIHelper.title("💰 Realizar Venta"), buildForm(), previewCard);
    }

    private VBox buildForm() {
        VBox card = UIHelper.card();

        Label lbl1 = UIHelper.sectionLabel("Título del videojuego:");
        titleField = UIHelper.styledField("Ej: The Witcher 3");
        titleField.setPrefWidth(350);

        Button previewBtn = UIHelper.secondaryButton("🔎 Verificar");
        previewBtn.setOnAction(e -> previewGame(titleField.getText().trim()));

        HBox row1 = new HBox(12, titleField, previewBtn);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label lbl2 = UIHelper.sectionLabel("Cantidad:");
        qtyField = UIHelper.styledField("Número de unidades");
        qtyField.setPrefWidth(200);

        Button sellBtn = UIHelper.primaryButton("💳 Confirmar Venta");
        sellBtn.setOnAction(e -> handleSell());

        Button clearBtn = UIHelper.secondaryButton("🔄 Limpiar");
        clearBtn.setOnAction(e -> { titleField.clear(); qtyField.clear(); previewCard.getChildren().clear(); });

        HBox btnRow = new HBox(12, sellBtn, clearBtn);

        card.getChildren().addAll(lbl1, row1, lbl2, qtyField, btnRow);
        return card;
    }

    private void previewGame(String title) {
        previewCard.getChildren().clear();
        if (title.isBlank()) return;
        VideoGame g = service.findByTitle(title);
        if (g == null) {
            Label err = UIHelper.bodyLabel("❌ Videojuego no encontrado.");
            err.setTextFill(Color.web("#ff6666"));
            previewCard.getChildren().add(UIHelper.card(err));
            return;
        }
        VBox card = UIHelper.card();
        Label head = new Label("📦 " + g.getTitle());
        head.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        head.setTextFill(Color.web("#c850c0"));

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(8);
        addRow(grid, 0, "Plataforma:", g.getPlatform());
        addRow(grid, 1, "Género:", g.getGenre());
        addRow(grid, 2, "Precio Final:", "$" + String.format("%.0f", g.calculateFinalPrice()));
        addRow(grid, 3, "Stock disponible:", String.valueOf(g.getStock()));

        Label stockNote = UIHelper.bodyLabel(g.getStock() == 0 ? "⚠️ Sin stock" : "✅ Disponible");
        stockNote.setTextFill(g.getStock() == 0 ? Color.web("#ff6666") : Color.web("#55ff99"));

        card.getChildren().addAll(head, grid, stockNote);
        previewCard.getChildren().add(card);
    }

    private void handleSell() {
        String title = titleField.getText().trim();
        String qtyStr = qtyField.getText().trim();

        if (title.isBlank()) {
            UIHelper.showWarning("Por favor ingresa el título del videojuego.");
            return;
        }
        if (qtyStr.isBlank()) {
            UIHelper.showWarning("Por favor ingresa la cantidad.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) { UIHelper.showWarning("La cantidad debe ser mayor a 0."); return; }

            Sale sale = service.sellVideoGame(title, qty);
            if (sale == null) {
                UIHelper.showWarning("❌ Videojuego no encontrado en el catálogo.\nVerifica el título e intenta de nuevo.");
                return;
            }

            // Show success receipt
            previewCard.getChildren().clear();
            VBox receipt = UIHelper.card();
            Label head = new Label("✅ Venta Exitosa");
            head.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            head.setTextFill(Color.web("#55ff99"));

            Label sep = new Label("─────────────────────────────────");
            sep.setTextFill(Color.web("#333355"));

            GridPane grid = new GridPane();
            grid.setHgap(20); grid.setVgap(10);
            addRow(grid, 0, "ID Venta:", sale.getId());
            addRow(grid, 1, "Videojuego:", sale.getVideoGame().getTitle());
            addRow(grid, 2, "Cantidad:", String.valueOf(sale.getQuantity()));
            addRow(grid, 3, "Precio unitario:", "$" + String.format("%.0f", sale.getUnitPrice()));
            addRow(grid, 4, "─────────────", "─────────────");

            Label totalLbl = new Label("TOTAL:");
            totalLbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            totalLbl.setTextFill(Color.web("#888899"));
            Label totalVal = new Label("$" + String.format("%.0f", sale.getTotal()));
            totalVal.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            totalVal.setTextFill(Color.web("#c850c0"));
            grid.add(totalLbl, 0, 5);
            grid.add(totalVal, 1, 5);

            receipt.getChildren().addAll(head, sep, grid);
            previewCard.getChildren().add(receipt);
            titleField.clear(); qtyField.clear();

        } catch (NumberFormatException ex) {
            UIHelper.showError("La cantidad debe ser un número entero.");
        } catch (IllegalStateException ex) {
            UIHelper.showWarning("⚠️ " + ex.getMessage());
        }
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = UIHelper.sectionLabel(label);
        lbl.setPrefWidth(150);
        Label val = UIHelper.bodyLabel(value);
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}
