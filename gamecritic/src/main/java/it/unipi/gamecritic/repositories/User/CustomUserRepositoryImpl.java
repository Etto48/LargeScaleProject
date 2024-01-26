//CustomUserRepositoryImpl

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.user.User;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBObject;

import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final MongoTemplate mongoTemplate;
    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public void insertUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("The given user must not be null");
        }
        mongoTemplate.insert(user, "users");
    }

    /// Returns true if the user was inserted, false if the user already exists
    public boolean insertUserIfNotExists(User user) {
        // Check if the user with the given username already exists
        Criteria criteria = Criteria.where("username").is(user.getUsername());
        Query query = new Query(criteria);

        if (!mongoTemplate.exists(query, User.class)) {
            // Insert the user only if it doesn't exist
            mongoTemplate.insert(user, "users");
            return true;
        } else {
            // Handle the case when the user already exists
            System.out.println("User with the given username already exists");
            return false;
        }
    }

    @Override
    public List<User> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        List<DBObject> user_dbos = mongoTemplate.find(query, DBObject.class, "users");
        List<User> users = user_dbos.stream().map(User::userFactory).toList();
        return users;
    }

    @Override
    public List<User> search(String query){
        if (query == null) {
            throw new IllegalArgumentException("The given query must not be null");
        }
        Criteria criteria = Criteria.where("username").regex(query, "i");
        Query q = new Query(criteria).limit(10);
        List<DBObject> user_dbos = mongoTemplate.find(q, DBObject.class, "users");
        List<User> users = user_dbos.stream().map(User::userFactory).toList();
        return users;
    }
}
