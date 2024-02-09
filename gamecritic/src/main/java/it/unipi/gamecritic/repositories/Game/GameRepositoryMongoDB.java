package it.unipi.gamecritic.repositories.Game;
import it.unipi.gamecritic.entities.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepositoryMongoDB extends MongoRepository<Game, String>, CustomGameRepository {

    
}
