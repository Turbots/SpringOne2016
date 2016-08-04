package be.ordina.jworks.rpsls.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessage {

    private String message;
    private String username;

}
