//CustomUserRepository

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.user.User;

import java.util.List;

public interface CustomUserRepository {
    //List<User> findByName(String Name);

    List<User> findByDynamicAttribute(String attributeName, String attributeValue);
    boolean insertUserIfNotExists(User user);
    void deleteUser(String username);
    // public List<User> findByUsername(String username);
    List<User> search(String query);
/*
    List<DBObject> findLatest(Integer offset);
    List<DBObject> findBest(Integer offset);
    List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);

 */
}
