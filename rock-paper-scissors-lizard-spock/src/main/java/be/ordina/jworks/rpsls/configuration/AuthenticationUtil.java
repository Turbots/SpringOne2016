package be.ordina.jworks.rpsls.configuration;

import be.ordina.jworks.rpsls.game.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;

import java.util.Collections;

@Slf4j
public class AuthenticationUtil {

    public static void authenticate(Connection<?> connection) {
        Player player = Player.builder()
                .username(connection.getDisplayName())
                .url(connection.getProfileUrl())
                .image(connection.getImageUrl())
                .build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(player.getUsername(), "", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.info("User {} connected successfully", player.getUsername());
    }
}
