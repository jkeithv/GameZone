package gamezone.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class HomePanel extends VBox {

    public HomePanel() {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(60));
        setStyle("-fx-background-color: #0f0f1a;");

        Label icon = new Label("🎮");
        icon.setFont(Font.font(80));

        Label title = new Label("Bienvenido a GameZone");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#c850c0"));

        Label sub = new Label("Sistema de Gestión de Videojuegos Digitales y Físicos");
        sub.setFont(Font.font("Arial", 15));
        sub.setTextFill(Color.web("#888899"));

        // Feature cards row
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(20, 0, 0, 0));

        cards.getChildren().addAll(
            featureCard("📦", "Catálogo", "Administra videojuegos\ndigitales y físicos"),
            featureCard("💰", "Ventas", "Registra ventas y\ncontrola el stock"),
            featureCard("🔍", "Búsquedas", "Busca por título\no plataforma"),
            featureCard("📊", "Estadísticas", "Consulta el historial\nde ventas")
        );

        Label hint = new Label("Usa el menú de la izquierda para navegar");
        hint.setFont(Font.font("Arial", 12));
        hint.setTextFill(Color.web("#555577"));

        getChildren().addAll(icon, title, sub, cards, hint);
    }

    private VBox featureCard(String emoji, String title, String desc) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 30, 25, 30));
        card.setPrefWidth(180);
        card.setStyle(
            "-fx-background-color: #1a1a2e; -fx-background-radius: 14;" +
            "-fx-border-color: #2a2a4a; -fx-border-radius: 14; -fx-border-width: 1;"
        );

        Label em = new Label(emoji);
        em.setFont(Font.font(32));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setTextFill(Color.web("#c850c0"));

        Label d = new Label(desc);
        d.setFont(Font.font("Arial", 12));
        d.setTextFill(Color.web("#888899"));
        d.setTextAlignment(TextAlignment.CENTER);
        d.setWrapText(true);

        card.getChildren().addAll(em, t, d);
        return card;
    }
}
