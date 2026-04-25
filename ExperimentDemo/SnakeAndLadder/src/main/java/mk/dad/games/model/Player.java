package mk.dad.games.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String id;
    private String name;
    private int currentPosition;
    private boolean isActive;
    private boolean hasWon;

    public void movePlayer(int diceValue) {
        this.currentPosition += diceValue;
        if (this.currentPosition > 100) {
            this.currentPosition = 100 - (this.currentPosition - 100);
        }
    }

    public void resetPosition() {
        this.currentPosition = 0;
        this.hasWon = false;
    }
}