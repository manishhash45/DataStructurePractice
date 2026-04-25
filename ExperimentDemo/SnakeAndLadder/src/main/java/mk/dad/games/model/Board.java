package mk.dad.games.model;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Board {
    private static final int BOARD_SIZE = 100;
    private final Map<Integer, Integer> snakesAndLadders;

    public Board() {
        this.snakesAndLadders = initializeBoard();
    }

    private Map<Integer, Integer> initializeBoard() {
        Map<Integer, Integer> board = new HashMap<>();

        // Ladders (start -> end)
        board.put(1, 38);
        board.put(4, 14);
        board.put(9, 31);
        board.put(21, 42);
        board.put(28, 84);
        board.put(51, 67);
        board.put(72, 91);
        board.put(80, 99);

        // Snakes (start -> end)
        board.put(17, 7);
        board.put(54, 34);
        board.put(62, 18);
        board.put(87, 24);
        board.put(93, 73);
        board.put(95, 75);
        board.put(98, 79);

        return board;
    }

    public int applySnakeOrLadder(int position) {
        if (position >= BOARD_SIZE) {
            return BOARD_SIZE;
        }
        return snakesAndLadders.getOrDefault(position, position);
    }

    public boolean isWinningPosition(int position) {
        return position == BOARD_SIZE;
    }
}