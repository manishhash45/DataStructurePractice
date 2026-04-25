package mk.dad.games.service;

import lombok.extern.slf4j.Slf4j;
import mk.dad.games.model.Game;
import mk.dad.games.model.GameStatus;
import mk.dad.games.model.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class GameService {
    private final Map<String, Game> games = new HashMap<>();
    private final Random random = new Random();

    public Game createGame() {
        Game game = new Game();
        games.put(game.getGameId(), game);
        log.info("Game created with ID: {}", game.getGameId());
        return game;
    }

    public Optional<Game> getGameById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    public void addPlayerToGame(String gameId, String playerId, String playerName) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }
        game.addPlayer(playerId, playerName);
        log.info("Player {} added to game {}", playerName, gameId);
    }

    public void startGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }
        game.startGame();
        log.info("Game {} started", gameId);
    }

    public void rollDiceAndMove(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }

        int diceValue = random.nextInt(6) + 1;
        game.rollDiceAndMove(diceValue);

        Player currentPlayer = game.getCurrentPlayer();
        log.info("Player {} rolled {} and moved to position {}",
                currentPlayer.getName(), diceValue, currentPlayer.getCurrentPosition());

        if (game.getWinner() != null) {
            log.info("Game {} finished! Winner: {}", gameId, game.getWinner().getName());
        }
    }

    public void resetGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found: " + gameId);
        }
        game.resetGame();
        log.info("Game {} reset", gameId);
    }

    public void endGame(String gameId) {
        Game game = games.get(gameId);
        if (game != null) {
            game.setStatus(GameStatus.FINISHED);
            log.info("Game {} ended", gameId);
        }
    }
}