package be.ordina.jworks.social.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.social.TwitterRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.social.security.SocialUserDetailsService;

@Configuration
@EnableRedisRepositories
@ConditionalOnClass(RedisConnectionFactory.class)
@AutoConfigureAfter(TwitterRedisAutoConfiguration.class)
@ComponentScan(basePackages = "be.ordina.jworks.social.redis.connection.data")
public class SocialRedisAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnBean(RedisConnectionFactory.class)
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate<byte[], byte[]>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(SocialUserDetailsService.class)
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService();
    }
}
