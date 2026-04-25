package mk.dad.games.controller;

import lombok.extern.slf4j.Slf4j;
import mk.dad.games.model.Game;
import mk.dad.games.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Create a new game
     * POST /api/games
     */
    @PostMapping
    public ResponseEntity<Game> createGame() {
        Game game = gameService.createGame();
        return ResponseEntity.ok(game);
    }

    /**
     * Get game by ID
     * GET /api/games/{gameId}
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable String gameId) {
        Optional<Game> game = gameService.getGameById(gameId);
        return game.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Add player to game
     * POST /api/games/{gameId}/players
     */
    @PostMapping("/{gameId}/players")
    public ResponseEntity<?> addPlayer(
            @PathVariable String gameId,
            @RequestBody Map<String, String> request) {
        try {
            String playerId = request.get("playerId");
            String playerName = request.get("playerName");
            gameService.addPlayerToGame(gameId, playerId, playerName);
            return ResponseEntity.ok(Map.of("message", "Player added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Start the game
     * POST /api/games/{gameId}/start
     */
    @PostMapping("/{gameId}/start")
    public ResponseEntity<?> startGame(@PathVariable String gameId) {
        try {
            gameService.startGame(gameId);
            Optional<Game> game = gameService.getGameById(gameId);
            return game.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Roll dice and move
     * POST /api/games/{gameId}/roll
     */
    @PostMapping("/{gameId}/roll")
    public ResponseEntity<?> rollDice(@PathVariable String gameId) {
        try {
            gameService.rollDiceAndMove(gameId);
            Optional<Game> game = gameService.getGameById(gameId);
            return game.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset the game
     * POST /api/games/{gameId}/reset
     */
    @PostMapping("/{gameId}/reset")
    public ResponseEntity<?> resetGame(@PathVariable String gameId) {
        try {
            gameService.resetGame(gameId);
            Optional<Game> game = gameService.getGameById(gameId);
            return game.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * End the game
     * POST /api/games/{gameId}/end
     */
    @PostMapping("/{gameId}/end")
    public ResponseEntity<?> endGame(@PathVariable String gameId) {
        gameService.endGame(gameId);
        return ResponseEntity.ok(Map.of("message", "Game ended successfully"));
    }
}