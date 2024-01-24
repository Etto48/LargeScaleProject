// UserRepository

package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.user.User;
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