package be.ordina.jworks.rpsls.game.pubsub;

import be.ordina.jworks.rpsls.game.websocket.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatEventPublisher {

    private final RedisTemplate<String, Object> messageRedisTemplate;
    private final Topic topic;

    @Autowired
    public ChatEventPublisher(final RedisTemplate<String, Object> messageRedisTemplate, @Qualifier("chatTopic") final Topic topic) {
        this.messageRedisTemplate = messageRedisTemplate;
        this.topic = topic;
    }

    public void publish(final ChatMessage chatMessage) {
        log.info("Sending ChatMessage " + chatMessage);
        this.messageRedisTemplate.convertAndSend(topic.getTopic(), chatMessage);
    }
}
