package gamezone.ui;

import gamezone.model.*;
import gamezone.service.VideoGameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class AddGamePanel extends ScrollPane {

    private final VideoGameService service;
    private TableView<VideoGame> table;

    // Form fields
    private ComboBox<String> typeCombo;
    private TextField titleField, priceField, platformField, stockField, genreField;
    private TextField sizeField, dlPlatformField, conditionField, distributorField;
    private VBox digitalFields, physicalFields;
    private Label formTitleLabel;
    private String editingTitle = null;

    public AddGamePanel(VideoGameService service) {
        this.service = service;
        setFitToWidth(true);
        setStyle("-fx-background-color: #0f0f1a; -fx-background: #0f0f1a;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #0f0f1a;");

        content.getChildren().addAll(
            UIHelper.title("➕ Gestión de Videojuegos"),
            buildForm(),
            buildTableSection()
        );

        setContent(content);
        loadTable();
    }

    private VBox buildForm() {
        VBox card = UIHelper.card();
        formTitleLabel = new Label("Agregar Nuevo Videojuego");
        formTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        formTitleLabel.setTextFill(Color.web("#c850c0"));

        // Type selector
        HBox typeRow = new HBox(10);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        typeCombo = UIHelper.styledCombo("Digital", "Físico");
        typeCombo.setValue("Digital");
        typeRow.getChildren().addAll(UIHelper.sectionLabel("Tipo:"), typeCombo);

        // Common fields
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        titleField    = UIHelper.styledField("Título del videojuego");
        priceField    = UIHelper.styledField("Precio base (ej: 59900)");
        platformField = UIHelper.styledField("Plataforma (ej: PC, PS5, Xbox)");
        stockField    = UIHelper.styledField("Cantidad en stock");
        genreField    = UIHelper.styledField("Género (ej: Acción, RPG)");

        addGridRow(grid, 0, "Título *", titleField);
        addGridRow(grid, 1, "Precio *", priceField);
        addGridRow(grid, 2, "Plataforma *", platformField);
        addGridRow(grid, 3, "Stock *", stockField);
        addGridRow(grid, 4, "Género *", genreField);

        // Digital-specific
        sizeField       = UIHelper.styledField("Tamaño en GB (ej: 45.5)");
        dlPlatformField = UIHelper.styledField("Plataforma descarga (ej: Steam)");
        digitalFields   = new VBox(10,
            fieldRow("Tamaño (GB) *", sizeField),
            fieldRow("Plataforma descarga *", dlPlatformField)
        );

        // Physical-specific
        conditionField  = UIHelper.styledField("Condición: nuevo / usado");
        distributorField= UIHelper.styledField("Distribuidor");
        physicalFields  = new VBox(10,
            fieldRow("Condición *", conditionField),
            fieldRow("Distribuidor *", distributorField)
        );
        physicalFields.setVisible(false);
        physicalFields.setManaged(false);

        typeCombo.setOnAction(e -> {
            boolean digital = "Digital".equals(typeCombo.getValue());
            digitalFields.setVisible(digital);
            digitalFields.setManaged(digital);
            physicalFields.setVisible(!digital);
            physicalFields.setManaged(!digital);
        });

        Button saveBtn  = UIHelper.primaryButton("💾 Guardar");
        Button clearBtn = UIHelper.secondaryButton("🔄 Limpiar");

        saveBtn.setOnAction(e -> handleSave());
        clearBtn.setOnAction(e -> clearForm());

        HBox btnRow = new HBox(12, saveBtn, clearBtn);

        card.getChildren().addAll(formTitleLabel, typeRow, grid, digitalFields, physicalFields, btnRow);
        return card;
    }

    private HBox fieldRow(String label, TextField field) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = UIHelper.sectionLabel(label);
        lbl.setPrefWidth(160);
        field.setPrefWidth(280);
        row.getChildren().addAll(lbl, field);
        return row;
    }

    private void addGridRow(GridPane grid, int row, String label, TextField field) {
        Label lbl = UIHelper.sectionLabel(label);
        lbl.setPrefWidth(120);
        field.setPrefWidth(340);
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    private VBox buildTableSection() {
        VBox section = UIHelper.card();
        Label lbl = new Label("📋 Videojuegos en Catálogo");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        lbl.setTextFill(Color.web("#c850c0"));

        table = new TableView<>();
        table.setStyle("-fx-background-color: #0f0f1a; -fx-text-fill: #eeeeee;");
        table.setPrefHeight(280);

        TableColumn<VideoGame, String> colTitle = col("Título", "title", 200);
        TableColumn<VideoGame, String> colType = new TableColumn<>("Tipo");
        colType.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
            cd.getValue() instanceof gamezone.model.DigitalVideoGame ? "Digital" : "Físico"
        ));
        colType.setPrefWidth(80);

        TableColumn<VideoGame, String> colPlatform = col("Plataforma", "platform", 100);
        TableColumn<VideoGame, String> colGenre    = col("Género", "genre", 100);
        TableColumn<VideoGame, Number> colPrice    = new TableColumn<>("Precio Final");
        colPrice.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleDoubleProperty(cd.getValue().calculateFinalPrice()));
        colPrice.setCellFactory(c -> new TableCell<VideoGame, Number>() {
            protected void updateItem(Number v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("$%.0f", v.doubleValue()));
            }
        });
        colPrice.setPrefWidth(110);

        TableColumn<VideoGame, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(cd ->
            new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getStock()).asObject());
        colStock.setPrefWidth(70);

        // Action columns
        TableColumn<VideoGame, Void> colEdit = new TableColumn<>("Editar");
        colEdit.setPrefWidth(80);
        colEdit.setCellFactory(c -> new TableCell<VideoGame, Void>() {
            private final Button btn = UIHelper.primaryButton("✏️");
            { btn.setOnAction(e -> { VideoGame g = getTableView().getItems().get(getIndex()); loadForEdit(g); }); }
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        TableColumn<VideoGame, Void> colDel = new TableColumn<>("Eliminar");
        colDel.setPrefWidth(90);
        colDel.setCellFactory(c -> new TableCell<VideoGame, Void>() {
            private final Button btn = UIHelper.dangerButton("🗑️");
            { btn.setOnAction(e -> { VideoGame g = getTableView().getItems().get(getIndex()); handleDelete(g); }); }
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        styleTable(table);
        table.getColumns().addAll(colTitle, colType, colPlatform, colGenre, colPrice, colStock, colEdit, colDel);

        Button refreshBtn = UIHelper.secondaryButton("🔄 Actualizar lista");
        refreshBtn.setOnAction(e -> loadTable());

        section.getChildren().addAll(lbl, table, refreshBtn);
        return section;
    }

    private void handleSave() {
        try {
            String title    = titleField.getText().trim();
            double price    = Double.parseDouble(priceField.getText().trim());
            String platform = platformField.getText().trim();
            int stock       = Integer.parseInt(stockField.getText().trim());
            String genre    = genreField.getText().trim();

            VideoGame game;
            if ("Digital".equals(typeCombo.getValue())) {
                double sizeGB = Double.parseDouble(sizeField.getText().trim());
                String dlPlatform = dlPlatformField.getText().trim();
                game = new gamezone.model.DigitalVideoGame(title, price, platform, stock, genre, sizeGB, dlPlatform);
            } else {
                String condition   = conditionField.getText().trim();
                String distributor = distributorField.getText().trim();
                game = new gamezone.model.PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
            }

            if (editingTitle != null) {
                boolean ok = service.updateGame(editingTitle, game);
                if (ok) { UIHelper.showSuccess("Videojuego actualizado exitosamente."); }
                else    { UIHelper.showError("No se encontró el videojuego a actualizar."); }
                editingTitle = null;
                formTitleLabel.setText("Agregar Nuevo Videojuego");
            } else {
                boolean added = service.addVideoGame(game);
                if (added) { UIHelper.showSuccess("Videojuego agregado al catálogo."); }
                else {
                    // Existing title → show popup as required
                    UIHelper.showWarning("El videojuego ya existe en el catálogo");
                }
            }
            clearForm();
            loadTable();
        } catch (NumberFormatException ex) {
            UIHelper.showError("Por favor ingresa valores numéricos válidos para precio, stock y tamaño.");
        } catch (IllegalArgumentException ex) {
            UIHelper.showError(ex.getMessage());
        }
    }

    private void handleDelete(VideoGame g) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar");
        confirm.setHeaderText("¿Eliminar \"" + g.getTitle() + "\"?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.deleteGame(g.getTitle());
                UIHelper.showSuccess("Videojuego eliminado.");
                loadTable();
            }
        });
    }

    private void loadForEdit(VideoGame g) {
        editingTitle = g.getTitle();
        formTitleLabel.setText("✏️ Editando: " + g.getTitle());
        titleField.setText(g.getTitle());
        priceField.setText(String.valueOf(g.getPrice()));
        platformField.setText(g.getPlatform());
        stockField.setText(String.valueOf(g.getStock()));
        genreField.setText(g.getGenre());

        if (g instanceof gamezone.model.DigitalVideoGame dg) {
            typeCombo.setValue("Digital");
            sizeField.setText(String.valueOf(dg.getSizeGB()));
            dlPlatformField.setText(dg.getDownloadPlatform());
            digitalFields.setVisible(true); digitalFields.setManaged(true);
            physicalFields.setVisible(false); physicalFields.setManaged(false);
        } else if (g instanceof gamezone.model.PhysicalVideoGame pg) {
            typeCombo.setValue("Físico");
            conditionField.setText(pg.getCondition());
            distributorField.setText(pg.getDistributor());
            physicalFields.setVisible(true); physicalFields.setManaged(true);
            digitalFields.setVisible(false); digitalFields.setManaged(false);
        }
    }

    private void clearForm() {
        editingTitle = null;
        formTitleLabel.setText("Agregar Nuevo Videojuego");
        titleField.clear(); priceField.clear(); platformField.clear();
        stockField.clear(); genreField.clear(); sizeField.clear();
        dlPlatformField.clear(); conditionField.clear(); distributorField.clear();
        typeCombo.setValue("Digital");
    }

    private void loadTable() {
        table.getItems().setAll(service.getAllGames());
    }

    private <T> TableColumn<VideoGame, T> col(String title, String prop, int width) {
        TableColumn<VideoGame, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
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
