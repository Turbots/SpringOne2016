package be.ordina.jworks.social.redis.connection.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@AllArgsConstructor
@RedisHash("socialRedisConnection")
public class SocialRedisConnection {

    @Id
    @Indexed
    private String providerUserId;

    @Indexed
    private String userId;

    @Indexed
    private String providerId;

    private String displayName;

    private String profileUrl;

    private String imageUrl;

    private String accessToken;

    private String secret;

    private String refreshToken;

    private Long expireTime;
}
