package gamezone.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import gamezone.service.VideoGameService;

public class MainApp extends Application {

    private VideoGameService service;
    private Stage primaryStage;
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.service = new VideoGameService();

        root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f1a;");

        root.setTop(buildHeader());
        root.setLeft(buildSidebar());
        showPanel(new HomePanel());

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("GameZone - Sistema de Gestión");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #7b2d8b; -fx-border-width: 0 0 2 0;");

        Label logo = new Label("🎮 GameZone");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web("#c850c0"));

        Label subtitle = new Label("  —  Sistema de Gestión de Videojuegos");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.web("#888888"));

        header.getChildren().addAll(logo, subtitle);
        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #1a1a2e;");

        Label menuLabel = new Label("MENÚ PRINCIPAL");
        menuLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        menuLabel.setTextFill(Color.web("#555577"));
        menuLabel.setPadding(new Insets(0, 0, 10, 5));

        String[][] items = {
            {"🏠", "Inicio"},
            {"➕", "Agregar Videojuego"},
            {"📋", "Listar Videojuegos"},
            {"🔍", "Buscar por Título"},
            {"🕹️", "Buscar por Plataforma"},
            {"💰", "Realizar Venta"},
            {"📊", "Mostrar Ventas"},
        };

        sidebar.getChildren().add(menuLabel);

        for (String[] item : items) {
            Button btn = new Button(item[0] + "  " + item[1]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setPadding(new Insets(12, 15, 12, 15));
            btn.setFont(Font.font("Arial", 13));
            btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ccccdd;" +
                "-fx-cursor: hand; -fx-background-radius: 8;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2a2a4a; -fx-text-fill: #c850c0;" +
                "-fx-cursor: hand; -fx-background-radius: 8;"
            ));
            btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ccccdd;" +
                "-fx-cursor: hand; -fx-background-radius: 8;"
            ));

            final String label = item[1];
            btn.setOnAction(e -> navigateTo(label));
            sidebar.getChildren().add(btn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button exitBtn = new Button("🚪  Salir");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setAlignment(Pos.CENTER_LEFT);
        exitBtn.setPadding(new Insets(12, 15, 12, 15));
        exitBtn.setFont(Font.font("Arial", 13));
        exitBtn.setStyle("-fx-background-color: #3a1a1a; -fx-text-fill: #ff6666; -fx-cursor: hand; -fx-background-radius: 8;");
        exitBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Salir");
            confirm.setHeaderText("¿Deseas salir del sistema?");
            confirm.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) primaryStage.close(); });
        });

        sidebar.getChildren().addAll(spacer, exitBtn);
        return sidebar;
    }

    private void navigateTo(String label) {
        switch (label) {
            case "Inicio"               -> showPanel(new HomePanel());
            case "Agregar Videojuego"   -> showPanel(new AddGamePanel(service));
            case "Listar Videojuegos"   -> showPanel(new ListGamesPanel(service));
            case "Buscar por Título"    -> showPanel(new SearchByTitlePanel(service));
            case "Buscar por Plataforma"-> showPanel(new SearchByPlatformPanel(service));
            case "Realizar Venta"       -> showPanel(new SalePanel(service));
            case "Mostrar Ventas"       -> showPanel(new SalesListPanel(service));
        }
    }

    private void showPanel(javafx.scene.Node panel) {
        root.setCenter(panel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
