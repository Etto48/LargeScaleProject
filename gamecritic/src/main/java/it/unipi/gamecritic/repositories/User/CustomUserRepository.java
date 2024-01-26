//CustomUserRepository

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.user.User;

import java.util.List;

public interface CustomUserRepository {
    //List<User> findByName(String Name);

    List<User> findByDynamicAttribute(String attributeName, String attributeValue);
    public boolean insertUserIfNotExists(User user);
    // public List<User> findByUsername(String username);
    List<User> search(String query);
/*
    List<DBObject> findLatest(Integer offset);
    List<DBObject> findBest(Integer offset);
    List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);

 */
}
