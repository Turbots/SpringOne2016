package be.ordina.jworks.rpsls.game.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class ChatEventConsumer {

    private final SimpMessagingTemplate template;

    public ChatEventConsumer(SimpMessagingTemplate template) {
        this.template = template;
    }

    @SuppressWarnings("unused")
    public void handleMessage(final String message) {
        log.debug("[CHAT] " + message);

        log.info("Sending ChatEvent to Websocket: " + message);
        template.convertAndSend("/topic/chat", message);
    }
}
