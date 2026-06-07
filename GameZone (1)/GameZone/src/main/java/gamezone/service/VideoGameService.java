package gamezone.service;

import gamezone.model.*;
import gamezone.repository.SaleRepository;
import gamezone.repository.VideoGameRepository;

import java.util.List;
import java.util.UUID;

public class VideoGameService {
    private final VideoGameRepository gameRepo;
    private final SaleRepository saleRepo;

    public VideoGameService() {
        this.gameRepo = new VideoGameRepository();
        this.saleRepo = new SaleRepository(gameRepo);
    }

    // ── ADD GAME ──────────────────────────────────────────────────────────────
    /**
     * @return true if added successfully, false if title already exists
     * @throws IllegalArgumentException if validation fails
     */
    public boolean addVideoGame(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío.");
        if (game.getPrice() <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        if (game.getStock() < 0)
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        return gameRepo.save(game);
    }

    // ── LIST ALL ──────────────────────────────────────────────────────────────
    public List<VideoGame> getAllGames() {
        return gameRepo.findAll();
    }

    // ── SEARCH BY TITLE ───────────────────────────────────────────────────────
    public VideoGame findByTitle(String title) {
        return gameRepo.findByTitle(title);
    }

    // ── SEARCH BY PLATFORM ────────────────────────────────────────────────────
    public List<VideoGame> findByPlatform(String platform) {
        return gameRepo.findByPlatform(platform);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean updateGame(String originalTitle, VideoGame newGame) {
        if (newGame.getTitle() == null || newGame.getTitle().isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío.");
        if (newGame.getPrice() <= 0)
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        if (newGame.getStock() < 0)
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        return gameRepo.update(originalTitle, newGame);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean deleteGame(String title) {
        return gameRepo.delete(title);
    }

    // ── SELL ──────────────────────────────────────────────────────────────────
    /**
     * @return Sale object if successful, null if game not found
     * @throws IllegalStateException if not enough stock
     */
    public Sale sellVideoGame(String title, int quantity) {
        VideoGame game = gameRepo.findByTitle(title);
        if (game == null) return null;
        if (game.getStock() < quantity)
            throw new IllegalStateException(
                "Stock insuficiente. Disponible: " + game.getStock());

        double unitPrice = game.calculateFinalPrice();
        game.setStock(game.getStock() - quantity);
        gameRepo.updateStock(game.getTitle(), game.getStock());

        String saleId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Sale sale = new Sale(saleId, game, quantity, unitPrice);
        saleRepo.save(sale);
        return sale;
    }

    // ── LIST SALES ────────────────────────────────────────────────────────────
    public List<Sale> getAllSales() {
        return saleRepo.findAll();
    }
}
