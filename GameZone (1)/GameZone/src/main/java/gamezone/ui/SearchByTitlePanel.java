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

public class SearchByTitlePanel extends VBox {

    private final VideoGameService service;
    private VBox resultCard;

    public SearchByTitlePanel(VideoGameService service) {
        this.service = service;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #0f0f1a;");

        resultCard = new VBox(10);

        getChildren().addAll(UIHelper.title("🔍 Buscar por Título"), buildSearchForm(), resultCard);
    }

    private VBox buildSearchForm() {
        VBox card = UIHelper.card();
        Label lbl = UIHelper.sectionLabel("Ingresa el título del videojuego:");
        TextField field = UIHelper.styledField("Ej: The Witcher 3");
        field.setPrefWidth(400);

        Button btn = UIHelper.primaryButton("🔍 Buscar");
        btn.setOnAction(e -> search(field.getText().trim()));
        field.setOnAction(e -> search(field.getText().trim()));

        HBox row = new HBox(12, field, btn);
        row.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(lbl, row);
        return card;
    }

    private void search(String query) {
        resultCard.getChildren().clear();
        if (query.isBlank()) {
            UIHelper.showWarning("Por favor ingresa un título para buscar.");
            return;
        }

        VideoGame g = service.findByTitle(query);
        if (g == null) {
            Label notFound = UIHelper.bodyLabel("❌ No se encontró ningún videojuego con el título: \"" + query + "\"");
            notFound.setTextFill(Color.web("#ff6666"));
            resultCard.getChildren().add(UIHelper.card(notFound));
            return;
        }

        VBox card = UIHelper.card();
        Label title = new Label("✅ Resultado encontrado");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#55ff99"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);

        addRow(grid, 0, "Título:",     g.getTitle());
        addRow(grid, 1, "Tipo:",       g instanceof DigitalVideoGame ? "Digital" : "Físico");
        addRow(grid, 2, "Plataforma:", g.getPlatform());
        addRow(grid, 3, "Género:",     g.getGenre());
        addRow(grid, 4, "Precio Base:", "$" + String.format("%.0f", g.getPrice()));
        addRow(grid, 5, "Precio Final:","$" + String.format("%.0f", g.calculateFinalPrice()));
        addRow(grid, 6, "Stock:",      String.valueOf(g.getStock()));

        if (g instanceof DigitalVideoGame dg) {
            addRow(grid, 7, "Tamaño:",     dg.getSizeGB() + " GB");
            addRow(grid, 8, "Descarga:",   dg.getDownloadPlatform());
        } else if (g instanceof PhysicalVideoGame pg) {
            addRow(grid, 7, "Condición:",   pg.getCondition());
            addRow(grid, 8, "Distribuidor:", pg.getDistributor());
        }

        card.getChildren().addAll(title, grid);
        resultCard.getChildren().add(card);
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = UIHelper.sectionLabel(label);
        lbl.setPrefWidth(140);
        Label val = UIHelper.bodyLabel(value);
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}
