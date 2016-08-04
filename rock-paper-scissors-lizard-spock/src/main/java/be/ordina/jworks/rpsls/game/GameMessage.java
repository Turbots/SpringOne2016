package be.ordina.jworks.rpsls.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GameMessage {

    private GameEvent gameEvent;
    private String username;
    private String imageUrl;

}
