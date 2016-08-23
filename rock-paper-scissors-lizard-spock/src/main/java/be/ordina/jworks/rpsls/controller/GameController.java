package be.ordina.jworks.rpsls.controller;

import be.ordina.jworks.rpsls.game.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class GameController {

    private final GameRepository gameRepository;

    @Autowired
    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessage chat(final ChatMessage chatMessage, final Principal principal) throws Exception {
        log.info("Received chat message {} from {}", chatMessage.getMessage(), principal.getName());
        return ChatMessage.builder()
                .message(chatMessage.getMessage())
                .username(principal.getName())
                .build();
    }

    @RequestMapping("/game/latest")
    public Game game(final Principal principal) {
        Optional<Game> game = findLatestGame();
        if (game.isPresent()) {
            log.debug("Sending game information for Game [{}] to [{}]", game.get().getId(), principal.getName());
            return game.get();
        } else {
            return Game.builder().build(); // empty game
        }
    }

    @MessageMapping("/game")
    @SendTo("/topic/game")
    public GameMessage gameEvent(final GameMessage gameMessage, final Principal principal) throws Exception {
        log.info("Received {} from {}", gameMessage.getGameEvent(), principal.getName());

        GameMessage.GameMessageBuilder gameMessageBuilder = GameMessage.builder();
        gameMessageBuilder.gameEvent(gameMessage.getGameEvent());
        gameMessageBuilder.username(principal.getName());

        switch (gameMessage.getGameEvent()) {
            case PLAYER_ONE_JOINED:
                validatePlayerOneJoined(principal.getName(), gameMessage.getImage());
                gameMessageBuilder.image(gameMessage.getImage());
                break;
            case PLAYER_ONE_LIZARD:
            case PLAYER_ONE_PAPER:
            case PLAYER_ONE_ROCK:
            case PLAYER_ONE_SCISSORS:
            case PLAYER_ONE_SPOCK:
                validatePlayerOneMoved(principal.getName(), gameMessage.getGameEvent());
                break;
            case PLAYER_TWO_JOINED:
                validatePlayerTwoJoined(principal.getName(), gameMessage.getImage());
                gameMessageBuilder.image(gameMessage.getImage());
                break;
            case PLAYER_TWO_LIZARD:
            case PLAYER_TWO_PAPER:
            case PLAYER_TWO_ROCK:
            case PLAYER_TWO_SCISSORS:
            case PLAYER_TWO_SPOCK:
                validatePlayerTwoMoved(principal.getName(), gameMessage.getGameEvent());
                break;
            case SPECTATOR_JOINED:
                gameMessageBuilder.image(gameMessage.getImage());
        }

        return gameMessageBuilder.build();
    }

    private Game validatePlayerOneJoined(final String playerName, final String playerImage) {
        Optional<Game> latestGame = findLatestGame();

        if (latestGame.isPresent()) {
            Game game = latestGame.get();
            if (game.getEnd() == null) {
                throw new IllegalStateException("Game [" + game.getId() + "] has already started");
            }
        }

        log.debug("Creating a new game for [{}]", playerName);
        return this.gameRepository.save(
                Game.builder()
                        .playerOne(playerName)
                        .playerOneImage(playerImage)
                        .start(LocalDateTime.now())
                        .build());
    }

    private Game validatePlayerTwoJoined(final String playerName, final String playerImage) {
        Game game = findLatestRunningGame();

        game.setPlayerTwo(playerName);
        game.setPlayerTwoImage(playerImage);

        log.debug("GAME [{}]: Adding PLAYER_TWO for [{}]", game.getId(), playerName);
        return this.gameRepository.save(game);
    }

    private Game validatePlayerOneMoved(final String playerName, final GameEvent gameEvent) {
        Game game = findLatestRunningGame();

        if (game.getPlayerOne().equals(playerName)) {
            game.setPlayerOneMove(gameEvent.ordinal());
            log.debug("GAME [{}]: PLAYER_ONE [{}] made his move: [{}]", game.getId(), playerName, gameEvent);
            return this.gameRepository.save(game);
        }

        throw new IllegalStateException("Player [" + playerName + "] is not PLAYER_ONE and cannot make this move");
    }

    private Game validatePlayerTwoMoved(final String playerName, final GameEvent gameEvent) {
        Game game = findLatestRunningGame();

        if (game.getPlayerTwo().equals(playerName)) {
            game.setPlayerTwoMove(gameEvent.ordinal());
            log.debug("GAME [{}]: PLAYER_TWO [{}] made his move: [{}]", game.getId(), playerName, gameEvent);
            return this.gameRepository.save(game);
        }

        throw new IllegalStateException("Player [" + playerName + "] is not PLAYER_TWO and cannot make this move!");
    }

    private Game findLatestRunningGame() {
        Optional<Game> latestGame = findLatestGame();

        if (latestGame.isPresent()) {
            Game game = latestGame.get();
            if (game.getEnd() == null) {
                return game;
            }

            throw new IllegalStateException("Game [" + game.getId() + "] already ended!");
        }

        throw new IllegalStateException("No games were found!");
    }

    private Optional<Game> findLatestGame() {
        List<Game> games = this.gameRepository.findAll();

        log.debug("Found [{}] games in the Redis repository", games.size());

        if (games.isEmpty()) {
            return Optional.empty();
        }

        Collections.sort(games.stream()
                .filter((game -> game == null || game.getId() == null))
                .collect(Collectors.toList()));

        return Optional.ofNullable(games.get(0));
    }
}
