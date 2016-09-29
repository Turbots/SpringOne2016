package be.ordina.jworks.rpsls.game;

import java.io.Serializable;

public enum GameEvent implements Serializable {

    SPECTATOR_JOINED(0),
    PLAYER_ONE_JOINED(0),
    PLAYER_TWO_JOINED(0),
    PLAYER_ONE_WINS(0),
    PLAYER_TWO_WINS(0),
    GAME_TIED(0),
    NO_WINNER_YET(0),
    PLAYER_ONE_ROCK(1),
    PLAYER_ONE_PAPER(2),
    PLAYER_ONE_SCISSORS(3),
    PLAYER_ONE_LIZARD(4),
    PLAYER_ONE_SPOCK(5),
    PLAYER_TWO_ROCK(1),
    PLAYER_TWO_PAPER(2),
    PLAYER_TWO_SCISSORS(3),
    PLAYER_TWO_LIZARD(4),
    PLAYER_TWO_SPOCK(5);

    private int value;

    GameEvent(final int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
