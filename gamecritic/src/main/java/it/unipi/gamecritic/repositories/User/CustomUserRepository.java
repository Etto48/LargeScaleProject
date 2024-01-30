//CustomUserRepository

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;

import java.util.List;

public interface CustomUserRepository {
    //List<User> findByName(String Name);

    void insertUser(User user);
    void updateUser(User user, boolean updatePassword, boolean updateEmail);
    List<User> findByDynamicAttribute(String attributeName, String attributeValue);
    boolean insertUserIfNotExists(User user);
    void deleteUser(String username);
    // public List<User> findByUsername(String username);
    List<User> search(String query);
    List<TopUserDTO> topUsersByReviews(Integer months);
/*
    List<DBObject> findLatest(Integer offset);
    List<DBObject> findBest(Integer offset);
    List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);

 */
}
