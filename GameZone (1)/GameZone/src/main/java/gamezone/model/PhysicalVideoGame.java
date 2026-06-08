package gamezone.model;

import gamezone.interfaces.Sellable;
import gamezone.interfaces.Tableable;

public class PhysicalVideoGame extends VideoGame implements Sellable, Tableable {
    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock,
                             String genre, String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition = condition;
        this.distributor = distributor;
    }

    public String getCondition() { return condition; }
    public String getDistributor() { return distributor; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setDistributor(String distributor) { this.distributor = distributor; }

    @Override
    public double calculateFinalPrice() {
        if ("usado".equalsIgnoreCase(condition)) {
            return price * 0.75;
        }
        return price;
    }

    @Override
    public double sell(int qty) {
        if (stock < qty) return -1;
        stock -= qty;
        return calculateFinalPrice() * qty;
    }

    @Override
    public String getDisplayInfo() {
        return "[FÍSICO] " + title + " | Plataforma: " + platform +
               " | Precio Final: $" + calculateFinalPrice() +
               " | Stock: " + stock + " | Condición: " + condition +
               " | Distribuidor: " + distributor;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[]{title, "Físico", platform, genre,
                            String.format("$%.2f", calculateFinalPrice()),
                            stock, condition, distributor};
    }

    @Override
    public String toString() {
        return "PhysicalVideoGame{title='" + title + "', price=" + price +
               ", platform='" + platform + "', stock=" + stock +
               ", genre='" + genre + "', condition='" + condition +
               "', distributor='" + distributor + "'}";
    }
}
