package be.ordina.jworks.rpsls.game.websocket;

import be.ordina.jworks.rpsls.game.Game;
import be.ordina.jworks.rpsls.game.GameEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage implements Serializable {

    private Game game;

    private GameEvent event;

    private String username;

    private String image;

    public static GameMessageBuilder of(GameMessage gameMessage) {
        return GameMessage.builder()
                .event(gameMessage.getEvent())
                .game(gameMessage.getGame())
                .image(gameMessage.getImage())
                .username(gameMessage.getUsername());
    }
}
