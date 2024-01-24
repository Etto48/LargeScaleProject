// UserRepository

package it.unipi.gamecritic.repositories;
import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import it.unipi.gamecritic.entities.user.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

//
public interface UserRepository extends MongoRepository<User, String>, CustomUserRepository {
    //User findByName(String name);
/*
    User findByName(String name);
    List<Game> findByName(String Name);
    List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue);
    List<DBObject> findLatest(Integer offset);
    List<DBObject> findBest(Integer offset);
    List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    List<DBObject> search(String query);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);


    public static List<Game> getMockupList() {}
    */
}