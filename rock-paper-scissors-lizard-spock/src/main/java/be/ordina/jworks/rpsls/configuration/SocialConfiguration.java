package be.ordina.jworks.rpsls.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.social.connect.web.SignInAdapter;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class SocialConfiguration {

    @Bean
    public SignInAdapter signInAdapter() {
        return (userId, connection, request) -> {
            AuthenticationUtil.authenticate(connection);
            return null;
        };
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(new JedisShardInfo("localhost"));
    }

}
