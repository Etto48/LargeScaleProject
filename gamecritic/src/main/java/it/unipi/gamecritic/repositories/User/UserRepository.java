package it.unipi.gamecritic.repositories.User;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;
import it.unipi.gamecritic.repositories.User.DTO.UserDTO;

@Component
public class UserRepository implements CustomUserRepository {
    private final UserRepositoryMongoDB userRepositoryMongoDB;
    private final UserRepositoryNeo4J userRepositoryNeo4J;

    public UserRepository(
        UserRepositoryMongoDB userRepositoryMongoDB, 
        UserRepositoryNeo4J userRepositoryNeo4J) 
    {
        this.userRepositoryMongoDB = userRepositoryMongoDB;
        this.userRepositoryNeo4J = userRepositoryNeo4J;
    }

    @Override
    @Deprecated
    public void insertUser(User user) {
        throw new UnsupportedOperationException("Use 'insertUserIfNotExists' instead");
    }

    @Override
    @Async
    public void updateUser(User user, boolean updatePassword, boolean updateEmail) {
        userRepositoryMongoDB.updateUser(user, updatePassword, updateEmail);
    }

    @Override
    public List<User> findByDynamicAttribute(String attributeName, String attributeValue) {
        return userRepositoryMongoDB.findByDynamicAttribute(attributeName, attributeValue);
    }

    @Override
    @Transactional
    public boolean insertUserIfNotExists(User user)
    {
        try {
            if(userRepositoryMongoDB.insertUserIfNotExists(user))
            {   
                userRepositoryNeo4J.insertUserIfNotExists(user.username);
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while inserting user");
        }
    }

    @Override
    @Transactional
    public void deleteUser(String username)
    {
        userRepositoryMongoDB.deleteUser(username);
        userRepositoryNeo4J.deleteUser(username);
    }

    @Override
    public List<User> search(String query) {
        return userRepositoryMongoDB.search(query);
    }

    @Override
    public List<TopUserDTO> topUsersByReviews(Integer months) {
        return userRepositoryMongoDB.topUsersByReviews(months);
    }

    @Override
    @Async
    public void updateTop3ReviewsByLikes() {
        userRepositoryMongoDB.updateTop3ReviewsByLikes();
    }

    @Async
    public void addFollowRelationship(String followerUsername, String followedUsername)
    {
        userRepositoryNeo4J.addFollowRelationship(followerUsername, followedUsername);
    }

    @Async
    public void removeFollowRelationship(String followerUsername, String followedUsername) 
    {
        userRepositoryNeo4J.removeFollowRelationship(followerUsername, followedUsername);
    }

    public Boolean getFollowRelationship(String followerUsername, String followedUsername)
    {
        return userRepositoryNeo4J.getFollowRelationship(followerUsername, followedUsername);
    }

    public List<UserDTO> findFollowed(String username)
    {
        return userRepositoryNeo4J.findFollowed(username);
    }

    public List<UserDTO> findFollowers(String username)
    {
        return userRepositoryNeo4J.findFollowers(username);
    }

    public List<UserDTO> findSuggestedUsers(String username)
    {
        return userRepositoryNeo4J.findSuggestedUsers(username);
    }

    public List<TopUserDTO> topUsersByLikes()
    {   
        return userRepositoryNeo4J.topUsersByLikes();
    }

}
