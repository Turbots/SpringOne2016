package be.ordina.jworks.rpsls.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.social.security.SpringSocialConfigurer;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public TextEncryptor textEncryptor() {
        return Encryptors.noOpText();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/game/**", "/app/**").authenticated()
                .and()
                .requestCache()
                .requestCache(new NullRequestCache())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies()
                .invalidateHttpSession(true)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .apply(securityConfigurer());
    }

    @Bean
    public SecurityConfigurer securityConfigurer() {
        return new SpringSocialConfigurer().connectionAddedRedirectUrl("/").postLoginUrl("/").alwaysUsePostLoginUrl(true);
    }

}
