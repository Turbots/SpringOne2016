package be.ordina.jworks.rpsls.controller;

import be.ordina.jworks.rpsls.game.*;
import be.ordina.jworks.rpsls.game.pubsub.ChatEventPublisher;
import be.ordina.jworks.rpsls.game.pubsub.GameEventPublisher;
import be.ordina.jworks.rpsls.game.websocket.ChatMessage;
import be.ordina.jworks.rpsls.game.websocket.GameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

@Slf4j
@RestController
public class GameController {

    private final GameRepository gameRepository;
    private final GameLogic gameLogic;
    private final ChatEventPublisher chatEventPublisher;
    private final GameEventPublisher gameEventPublisher;

    @Autowired
    public GameController(final GameRepository gameRepository, final GameLogic gameLogic, ChatEventPublisher chatEventPublisher, final GameEventPublisher gameEventPublisher) {
        this.gameRepository = gameRepository;
        this.gameLogic = gameLogic;
        this.chatEventPublisher = chatEventPublisher;
        this.gameEventPublisher = gameEventPublisher;
    }

    @RequestMapping("/game/player")
    public Player getPlayer() {
        return (Player) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @MessageMapping("/chat")
    public void chat(final ChatMessage chatMessage, final Principal principal) throws Exception {
        log.info("Chat >> [{}] >> [{}]", principal.getName(), chatMessage.getMessage());

        ChatMessage message = ChatMessage.builder()
                .message(chatMessage.getMessage())
                .username(principal.getName())
                .build();

        this.chatEventPublisher.publish(message);
    }

    @MessageMapping("/game")
    public void event(final GameMessage gameMessage, final Principal principal) throws Exception {
        log.info("Player [{}] >> [{}]", principal.getName(), gameMessage.getEvent());

        switch (gameMessage.getEvent()) {
            case PLAYER_ONE_JOINED:
                publish(playerOneJoined(principal.getName(), gameMessage));
                break;
            case PLAYER_ONE_LIZARD:
            case PLAYER_ONE_PAPER:
            case PLAYER_ONE_ROCK:
            case PLAYER_ONE_SCISSORS:
            case PLAYER_ONE_SPOCK:
                publish(playerOneMoved(principal.getName(), gameMessage));
                break;
            case PLAYER_TWO_JOINED:
                publish(playerTwoJoined(principal.getName(), gameMessage));
                break;
            case PLAYER_TWO_LIZARD:
            case PLAYER_TWO_PAPER:
            case PLAYER_TWO_ROCK:
            case PLAYER_TWO_SCISSORS:
            case PLAYER_TWO_SPOCK:
                publish(playerTwoMoved(principal.getName(), gameMessage));
                break;
            case SPECTATOR_JOINED:
                publish(spectatorJoined(principal.getName(), gameMessage));
                break;
            default:
                throw new IllegalStateException("GameEvent [" + gameMessage.getEvent() + "] is not allowed!");
        }
    }

    private void publish(final GameMessage gameMessage) {
        this.gameEventPublisher.publish(gameMessage);
    }

    private GameMessage playerOneJoined(final String username, final GameMessage gameMessage) {
        Optional<Game> latestGame = findLatestGame();

        if (latestGame.isPresent()) {
            Game latestGameInRepo = latestGame.get();
            if (latestGameInRepo.getEnd() == null) {
                throw new IllegalStateException("Game [" + latestGameInRepo.getId() + "] has already started");
            }
        }

        Game game = this.gameRepository.save(
                Game.builder()
                        .playerOne(username)
                        .playerOneImage(gameMessage.getImage())
                        .start(LocalDateTime.now())
                        .build());
        log.info("Game [{}] >> [{}] >> JOINED (P1)", game.getId(), username);

        return GameMessage.of(gameMessage).game(game).build();
    }

    private GameMessage playerTwoJoined(final String username, final GameMessage gameMessage) {
        notNull(gameMessage.getGame(), "Game object should at least contain the Game ID");

        Game currentGame = findRequiredGame(gameMessage.getGame().getId());

        if (currentGame.getPlayerOne().equals(username)) {
            throw new IllegalStateException("GAME [{" + gameMessage.getGame().getId() + "}] cannot be the same as PLAYER_ONE");
        }

        currentGame.setPlayerTwo(username);
        currentGame.setPlayerTwoImage(gameMessage.getImage());

        log.info("Game [{}] >> [{}] >> JOINED (P2)", currentGame.getId(), username);
        Game game = this.gameRepository.save(currentGame);

        return GameMessage.of(gameMessage).game(game).build();
    }

    private GameMessage playerOneMoved(final String username, final GameMessage gameMessage) {
        notNull(gameMessage.getGame(), "Game object should at least contain the Game ID");

        Game currentGame = findRequiredGame(gameMessage.getGame().getId());

        if (currentGame.getPlayerOne().equals(username)) {
            currentGame.setPlayerOneMove(gameMessage.getEvent().value());
            log.info("Game [{}] >> [{}] >> [{}]", currentGame.getId(), username, gameMessage.getEvent());
            currentGame = this.gameRepository.save(currentGame);

            GameEvent gameEvent = gameLogic.decideWinner(currentGame);

            currentGame = decideGame(currentGame, gameEvent);

            return GameMessage.of(gameMessage).event(gameEvent).game(currentGame).build();
        }

        throw new IllegalStateException("Game [{}]: Player [" + username + "] is not PLAYER_ONE and cannot make this move");
    }

    private GameMessage playerTwoMoved(final String username, final GameMessage gameMessage) {
        notNull(gameMessage.getGame(), "Game object should at least contain the Game ID");

        Game currentGame = findRequiredGame(gameMessage.getGame().getId());

        if (currentGame.getPlayerTwo().equals(username)) {
            currentGame.setPlayerTwoMove(gameMessage.getEvent().value());
            log.info("Game [{}] >> [{}] >> [{}]", currentGame.getId(), username, gameMessage.getEvent());
            currentGame = this.gameRepository.save(currentGame);

            GameEvent gameEvent = gameLogic.decideWinner(currentGame);

            currentGame = decideGame(currentGame, gameEvent);

            return GameMessage.of(gameMessage).event(gameEvent).game(currentGame).build();
        }

        throw new IllegalStateException("Player [" + username + "] is not PLAYER_TWO and cannot make this move!");
    }

    private Game decideGame(Game currentGame, final GameEvent gameEvent) {
        // don't want to send the moves to the client before both of them moved
        if (gameEvent == GameEvent.NO_WINNER_YET) {
            currentGame.setPlayerOneMove(0);
            currentGame.setPlayerTwoMove(0);
        } else {
            currentGame.setEnd(LocalDateTime.now());
            currentGame = this.gameRepository.save(currentGame);
        }
        return currentGame;
    }

    private GameMessage spectatorJoined(final String username, final GameMessage gameMessage) {
        Optional<Game> latestGame = findLatestGame();
        return GameMessage.of(gameMessage).username(username).game(latestGame.isPresent() ? latestGame.get() : null).build();
    }

    private Game findRequiredGame(String id) {
        Game game = this.gameRepository.findOne(id);
        if (game == null) {
            throw new IllegalStateException("Could not find game with ID [" + id + "]");
        }

        return game;
    }

    private Optional<Game> findLatestGame() {
        List<Game> games = this.gameRepository.findAll();

        log.debug("Found [{}] games in the Redis repository", games.size());

        if (games.isEmpty()) {
            return Optional.empty();
        }

        List<Game> sortedGames = games.stream()
                .filter((game -> game != null && game.getId() != null))
                .collect(Collectors.toList());

        Collections.sort(sortedGames);

        return sortedGames.isEmpty() ? Optional.empty() : Optional.ofNullable(sortedGames.get(0));
    }
}
