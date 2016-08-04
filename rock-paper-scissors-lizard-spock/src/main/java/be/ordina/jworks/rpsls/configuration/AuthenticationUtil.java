package be.ordina.jworks.rpsls.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;

import java.util.Collections;

@Slf4j
public class AuthenticationUtil {

    public static void authenticate(Connection<?> connection) {
        String displayName = connection.getDisplayName();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(displayName, "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("User {} connected successfully", displayName);
    }
}
