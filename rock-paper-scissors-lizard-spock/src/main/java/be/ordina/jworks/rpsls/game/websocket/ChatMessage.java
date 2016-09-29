package be.ordina.jworks.rpsls.game.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessage implements Serializable {

    private String message;
    private String username;

}
