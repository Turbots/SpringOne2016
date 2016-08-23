package be.ordina.jworks.rpsls.controller;

import be.ordina.jworks.rpsls.game.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/game/player")
public class PlayerController {

    private final ConnectionRepository connectionRepository;

    @Autowired
    public PlayerController(final ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Player> userSession(final HttpSession session) {
        try {
            Connection<Twitter> connection = connectionRepository.getPrimaryConnection(Twitter.class);

            if (connection != null) {
                Player player = Player.builder()
                        .name(connection.getDisplayName())
                        .image(connection.getImageUrl())
                        .url(connection.getProfileUrl()).build();

                session.setAttribute("player", player);
            }

            Player player = (Player) session.getAttribute("player");

            return new ResponseEntity<>(player, HttpStatus.OK);
        } catch (NotConnectedException e) {
            log.error("Could not GET player", e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
