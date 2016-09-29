package be.ordina.jworks.rpsls.game.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class GameEventConsumer {

    private final SimpMessagingTemplate template;

    public GameEventConsumer(SimpMessagingTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("unused")
    public void handleMessage(final String message) {
        log.info("[GAME] " + message);

        log.info("Sending GameEvent to Websocket: " + message);
        template.convertAndSend("/topic/game", message);
    }
}
