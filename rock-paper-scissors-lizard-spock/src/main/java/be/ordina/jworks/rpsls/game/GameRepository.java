package be.ordina.jworks.rpsls.game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {

    List<Game> findAll();

    Game findByPlayerOne(final String playerOne);

}
