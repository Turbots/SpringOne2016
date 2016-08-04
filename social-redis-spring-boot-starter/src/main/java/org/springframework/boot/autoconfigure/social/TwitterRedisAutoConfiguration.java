package org.springframework.boot.autoconfigure.social;

import be.ordina.jworks.social.redis.connection.RedisUsersConnectionRepository;
import be.ordina.jworks.social.redis.connection.data.SocialRedisConnectionRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.*;
import org.springframework.social.connect.web.GenericConnectionStatusView;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

@Configuration
@ConditionalOnClass({SocialConfigurerAdapter.class, TwitterConnectionFactory.class})
@ConditionalOnProperty(prefix = "social.redis.twitter", name = "app-id")
@AutoConfigureAfter(SocialWebAutoConfiguration.class)
public class TwitterRedisAutoConfiguration {

    @Configuration
    @EnableSocial
    @EnableConfigurationProperties(TwitterRedisProperties.class)
    @ConditionalOnWebApplication
    protected static class TwitterConfigurerAdapter extends SocialAutoConfigurerAdapter {

        private final TwitterRedisProperties properties;
        private final SocialRedisConnectionRepository socialRedisConnectionRepository;

        protected TwitterConfigurerAdapter(TwitterRedisProperties properties, SocialRedisConnectionRepository socialRedisConnectionRepository) {
            this.properties = properties;
            this.socialRedisConnectionRepository = socialRedisConnectionRepository;
        }

        @Bean
        @ConditionalOnMissingBean
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public Twitter twitter(ConnectionRepository repository) {
            Connection<Twitter> connection = repository
                    .findPrimaryConnection(Twitter.class);
            if (connection != null) {
                return connection.getApi();
            }
            return new TwitterTemplate(this.properties.getAppId(),
                    this.properties.getAppSecret());
        }

        @Bean
        @Primary
        @ConditionalOnBean(RedisConnectionFactory.class)
        @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
        public ConnectionRepository connectionRepository(UsersConnectionRepository usersConnectionRepository) {
            return usersConnectionRepository.createConnectionRepository(getUserIdSource().getUserId());
        }

        @Bean(name = {"connect/twitterConnect", "connect/twitterConnected"})
        @ConditionalOnProperty(prefix = "spring.social", name = "auto-connection-views")
        public GenericConnectionStatusView twitterConnectView() {
            return new GenericConnectionStatusView("twitter", "Twitter");
        }

        @Bean
        @ConditionalOnMissingBean
        public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
            return new ProviderSignInController(connectionFactoryLocator, usersConnectionRepository, new SpringSecuritySignInAdapter());
        }

        private class SpringSecuritySignInAdapter implements SignInAdapter {
            public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(localUserId, null, null));
                return null;
            }
        }

        @Override
        protected ConnectionFactory<?> createConnectionFactory() {
            return new TwitterConnectionFactory(this.properties.getAppId(),
                    this.properties.getAppSecret());
        }

        @Override
        public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
            return new RedisUsersConnectionRepository(connectionFactoryLocator, Encryptors.noOpText(), socialRedisConnectionRepository);
        }

        @Override
        public UserIdSource getUserIdSource() {
            return () -> {
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = context.getAuthentication();
                Assert.state(authentication != null, "Cannot find the authenticated user");
                return authentication.getName();
            };
        }
    }

    @ConfigurationProperties("social.redis.twitter")
    public class TwitterRedisProperties {

        private String appId;
        private String appSecret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }
}
