package be.ordina.jworks.rpsls.controller;

import be.ordina.jworks.rpsls.game.ChatMessage;
import be.ordina.jworks.rpsls.game.GameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class GameController {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessage chat(final ChatMessage chatMessage, final Principal principal) throws Exception {
        log.info("Received chat message {} from {}", chatMessage.getMessage(), principal.getName());
        return ChatMessage.builder()
                .message(chatMessage.getMessage())
                .username(principal.getName())
                .build();
    }

    @MessageMapping("/event")
    @SendTo("/topic/event")
    public GameMessage event(final GameMessage gameMessage, final Principal principal) throws Exception {
        log.info("Received {} from {}", gameMessage.getGameEvent(), principal.getName());

        return GameMessage.builder()
                .gameEvent(gameMessage.getGameEvent())
                .username(principal.getName())
                .imageUrl(gameMessage.getImageUrl())
                .build();
    }
}
