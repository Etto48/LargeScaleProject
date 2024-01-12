package it.unipi.gamecritic.repositories;
import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
//
public interface GameRepository extends MongoRepository<Game, String>, CustomGameRepository{

}
