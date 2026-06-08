package gamezone.repository;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.model.VideoGame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository {
    private static final String FILE_PATH = "data/videogames.json";

    public VideoGameRepository() {
        new File("data").mkdirs();
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try { f.createNewFile(); Files.writeString(Paths.get(FILE_PATH), "[]"); }
            catch (IOException ignored) {}
        }
    }

    // READ ALL
    public List<VideoGame> findAll() {
        List<VideoGame> list = new ArrayList<>();
        try {
            String content = Files.readString(Paths.get(FILE_PATH));
            if (content.isBlank()) return list;
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                VideoGame vg = parseGame(obj);
                if (vg != null) list.add(vg);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    // READ BY TITLE (case-insensitive)
    public VideoGame findByTitle(String title) {
        return findAll().stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(title))
                .findFirst().orElse(null);
    }

    // READ BY PLATFORM (case-insensitive)
    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame g : findAll()) {
            if (g.getPlatform().equalsIgnoreCase(platform)) result.add(g);
        }
        return result.isEmpty() ? null : result;
    }

    // CREATE
    public boolean save(VideoGame game) {
        List<VideoGame> list = findAll();
        boolean exists = list.stream()
                .anyMatch(g -> g.getTitle().equalsIgnoreCase(game.getTitle()));
        if (exists) return false; // caller shows alert
        list.add(game);
        writeAll(list);
        return true;
    }

    // UPDATE
    public boolean update(String title, VideoGame newGame) {
        List<VideoGame> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().equalsIgnoreCase(title)) {
                list.set(i, newGame);
                writeAll(list);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(String title) {
        List<VideoGame> list = findAll();
        boolean removed = list.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) writeAll(list);
        return removed;
    }

    // UPDATE STOCK (after sale)
    public void updateStock(String title, int newStock) {
        List<VideoGame> list = findAll();
        for (VideoGame g : list) {
            if (g.getTitle().equalsIgnoreCase(title)) {
                g.setStock(newStock);
                break;
            }
        }
        writeAll(list);
    }

    private void writeAll(List<VideoGame> list) {
        JSONArray arr = new JSONArray();
        for (VideoGame g : list) arr.put(toJson(g));
        try { Files.writeString(Paths.get(FILE_PATH), arr.toString(2)); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private JSONObject toJson(VideoGame g) {
        JSONObject obj = new JSONObject();
        obj.put("type", g instanceof DigitalVideoGame ? "digital" : "physical");
        obj.put("title", g.getTitle());
        obj.put("price", g.getPrice());
        obj.put("platform", g.getPlatform());
        obj.put("stock", g.getStock());
        obj.put("genre", g.getGenre());
        if (g instanceof DigitalVideoGame dg) {
            obj.put("sizeGB", dg.getSizeGB());
            obj.put("downloadPlatform", dg.getDownloadPlatform());
        } else if (g instanceof PhysicalVideoGame pg) {
            obj.put("condition", pg.getCondition());
            obj.put("distributor", pg.getDistributor());
        }
        return obj;
    }

    private VideoGame parseGame(JSONObject obj) {
        String type = obj.getString("type");
        String title = obj.getString("title");
        double price = obj.getDouble("price");
        String platform = obj.getString("platform");
        int stock = obj.getInt("stock");
        String genre = obj.getString("genre");
        if ("digital".equals(type)) {
            double sizeGB = obj.getDouble("sizeGB");
            String dlPlatform = obj.getString("downloadPlatform");
            return new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, dlPlatform);
        } else {
            String condition = obj.getString("condition");
            String distributor = obj.getString("distributor");
            return new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
        }
    }
}
