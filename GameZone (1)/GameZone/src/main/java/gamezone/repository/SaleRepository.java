package gamezone.repository;

import gamezone.model.Sale;
import gamezone.model.VideoGame;
import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {
    private static final String FILE_PATH = "data/sales.json";
    private final VideoGameRepository gameRepo;

    public SaleRepository(VideoGameRepository gameRepo) {
        this.gameRepo = gameRepo;
        new File("data").mkdirs();
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try { f.createNewFile(); Files.writeString(Paths.get(FILE_PATH), "[]"); }
            catch (IOException ignored) {}
        }
    }

    public List<Sale> findAll() {
        List<Sale> list = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(FILE_PATH));
            if (content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("id");
                String gameTitle = obj.getString("gameTitle");
                int qty = obj.getInt("quantity");
                double unitPrice = obj.getDouble("unitPrice");
                VideoGame game = gameRepo.findByTitle(gameTitle);
                if (game != null) {
                    Sale s = new Sale(id, game, qty, unitPrice);
                    list.add(s);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public void save(Sale sale) {
        List<Sale> list = findAll();
        list.add(sale);
        JSONArray arr = new JSONArray();
        for (Sale s : list) {
            JSONObject obj = new JSONObject();
            obj.put("id", s.getId());
            obj.put("gameTitle", s.getVideoGame().getTitle());
            obj.put("quantity", s.getQuantity());
            obj.put("unitPrice", s.getUnitPrice());
            obj.put("total", s.getTotal());
            obj.put("saleDate", s.getSaleDate().toString());
            arr.put(obj);
        }
        try { Files.writeString(Paths.get(FILE_PATH), arr.toString(2)); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
