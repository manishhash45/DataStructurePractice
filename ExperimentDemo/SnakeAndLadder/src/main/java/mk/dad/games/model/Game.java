package mk.dad.games.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class Game {
    private String gameId;
    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;
    private GameStatus status;
    private Player winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Game() {
        this.gameId = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.board = new Board();
        this.currentPlayerIndex = 0;
        this.status = GameStatus.WAITING;
        this.startTime = LocalDateTime.now();
    }

    public void addPlayer(String playerId, String playerName) {
        if (status != GameStatus.WAITING) {
            throw new IllegalStateException("Cannot add players to an active game");
        }
        Player player = new Player(playerId, playerName, 0, true, false);
        players.add(player);
    }

    public void startGame() {
        if (players.size() < 2) {
            throw new IllegalStateException("Minimum 2 players required to start the game");
        }
        this.status = GameStatus.IN_PROGRESS;
    }

    public void rollDiceAndMove(int diceValue) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        if (diceValue < 1 || diceValue > 6) {
            throw new IllegalArgumentException("Dice value must be between 1 and 6");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.movePlayer(diceValue);

        int newPosition = board.applySnakeOrLadder(currentPlayer.getCurrentPosition());
        currentPlayer.setCurrentPosition(newPosition);

        // Check for winning condition
        if (board.isWinningPosition(newPosition)) {
            currentPlayer.setHasWon(true);
            this.winner = currentPlayer;
            this.status = GameStatus.FINISHED;
            this.endTime = LocalDateTime.now();
        } else {
            moveToNextPlayer();
        }
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void resetGame() {
        players.forEach(Player::resetPosition);
        currentPlayerIndex = 0;
        status = GameStatus.IN_PROGRESS;
        winner = null;
        startTime = LocalDateTime.now();
        endTime = null;
    }

    public Optional<Player> findPlayerById(String playerId) {
        return players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst();
    }
}