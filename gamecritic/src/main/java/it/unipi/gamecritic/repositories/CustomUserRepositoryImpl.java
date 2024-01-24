//CustomUserRepositoryImpl

package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Company;
import it.unipi.gamecritic.entities.user.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final MongoTemplate mongoTemplate;
    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public void insertUser(User user) {
        mongoTemplate.insert(user, "users");
    }

    public void insertUserIfNotExists(User user) {
        // Check if the user with the given username already exists
        Criteria criteria = Criteria.where("username").is(user.getUsername());
        Query query = new Query(criteria);

        if (!mongoTemplate.exists(query, User.class)) {
            // Insert the user only if it doesn't exist
            mongoTemplate.insert(user, "users");
        } else {
            // Handle the case when the user already exists
            System.out.println("User with the given username already exists");
        }
    }

    @Override
    public List<User> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            return null;
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        List<DBObject> l = mongoTemplate.find(query, DBObject.class, "users");
        return mongoTemplate.find(query, User.class, "users");
    }

    @Override
    public List<User> search(String query){
        Criteria criteria = Criteria.where("username").regex(query, "i");
        Query q = new Query(criteria).limit(10);
        return mongoTemplate.find(q, User.class, "users");
    }
}
