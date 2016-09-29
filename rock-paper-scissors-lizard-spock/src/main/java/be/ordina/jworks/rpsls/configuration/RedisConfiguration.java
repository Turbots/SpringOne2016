package be.ordina.jworks.rpsls.configuration;

import be.ordina.jworks.rpsls.game.pubsub.ChatEventConsumer;
import be.ordina.jworks.rpsls.game.pubsub.GameEventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import redis.clients.jedis.JedisShardInfo;

@Slf4j
@Configuration
@EnableRedisRepositories(basePackages = "be.ordina.jworks.rpsls.game")
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(new JedisShardInfo("localhost"));
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(SimpMessagingTemplate simpMessagingTemplate) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(gameMessageListener(simpMessagingTemplate), gameTopic());
        container.addMessageListener(chatMessageListener(simpMessagingTemplate), chatTopic());

        return container;
    }

    @Bean
    public RedisTemplate<String, Object> messageRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JsonRedisSerializer());

        return template;
    }

    @Bean
    public MessageListener gameMessageListener(SimpMessagingTemplate simpMessagingTemplate) {
        return new MessageListenerAdapter(new GameEventConsumer(simpMessagingTemplate));
    }

    @Bean
    public Topic gameTopic() {
        return new ChannelTopic("pubsub:game");
    }

    @Bean
    public MessageListener chatMessageListener(SimpMessagingTemplate simpMessagingTemplate) {
        return new MessageListenerAdapter(new ChatEventConsumer(simpMessagingTemplate));
    }

    @Bean
    public Topic chatTopic() {
        return new ChannelTopic("pubsub:chat");
    }

}
