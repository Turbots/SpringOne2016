package be.ordina.jworks.rpsls.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage {

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
