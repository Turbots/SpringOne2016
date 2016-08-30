package be.ordina.jworks.rpsls.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GameLogic {

    public GameEvent decideWinner(final Game game) {
        log.info("Game [{}]: Deciding Winner", game.getId());

        int playerOneMove = game.getPlayerOneMove();
        int playerTwoMove = game.getPlayerTwoMove();

        if (playerOneMove == 0 || playerTwoMove == 0) {
            return GameEvent.NO_WINNER_YET;
        }

        if (playerOneMove == playerTwoMove) {
            return GameEvent.GAME_TIED;
        }

        switch (playerOneMove) {
            case 1:
                return (playerTwoMove == 3 || playerTwoMove == 4) ? GameEvent.PLAYER_ONE_WINS : GameEvent.PLAYER_TWO_WINS;
            case 2:
                return (playerTwoMove == 1 || playerTwoMove == 5) ? GameEvent.PLAYER_ONE_WINS : GameEvent.PLAYER_TWO_WINS;
            case 3:
                return (playerTwoMove == 2 || playerTwoMove == 4) ? GameEvent.PLAYER_ONE_WINS : GameEvent.PLAYER_TWO_WINS;
            case 4:
                return (playerTwoMove == 2 || playerTwoMove == 5) ? GameEvent.PLAYER_ONE_WINS : GameEvent.PLAYER_TWO_WINS;
            case 5:
                return (playerTwoMove == 1 || playerTwoMove == 3) ? GameEvent.PLAYER_ONE_WINS : GameEvent.PLAYER_TWO_WINS;
            default: {
                throw new IllegalStateException("GAME [" + game.getId() + "] : Invalid moves [" + playerOneMove + "] and [" + playerTwoMove + "]");
            }
        }
    }
}
