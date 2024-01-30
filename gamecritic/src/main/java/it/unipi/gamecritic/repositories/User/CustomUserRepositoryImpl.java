//CustomUserRepositoryImpl

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.entities.UserImage;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;

import com.mongodb.DBObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final MongoTemplate mongoTemplate;
    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public void insertUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("The given user must not be null");
        }
        mongoTemplate.insert(user, "users");
    }
    @Override
    @Async
    public void updateUser(User user, boolean updatePassword, boolean updateEmail) {
        if (user == null) {
            throw new IllegalArgumentException("The given user must not be null");
        }
        if(!updatePassword && !updateEmail)
        {
            return;
        }
        Query query = new Query(Criteria.where("username").is(user.username));
        Update update = new Update();
        if(updatePassword)
        {
            update.set("password_hash", user.password_hash);
        }
        if(updateEmail)
        {
            update.set("email", user.email);
        }
        
        mongoTemplate.updateFirst(query, update,  User.class, "users");
    }


    /** 
     * Returns true if the user was inserted, false if the user already exists
     **/
    @Override
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
    @Async
    public void deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("The given username must not be null");
        }
        Query usersQuery = new Query(Criteria.where("username").is(username));
        Query reviewsAndCommentsQuery = new Query(Criteria.where("author").is(username));
        Query imageQuery = new Query(Criteria.where("username").is(username));
        mongoTemplate.remove(usersQuery, User.class, "users");
        mongoTemplate.remove(imageQuery, UserImage.class, "user_images");
        mongoTemplate.remove(reviewsAndCommentsQuery, Review.class, "reviews");
        mongoTemplate.remove(reviewsAndCommentsQuery, Comment.class, "comments");
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

    @Override
    public List<TopUserDTO> topUsersByReviews(Integer months) {
        if (months == null || months < 1) {
            throw new IllegalArgumentException("The given months must not be null nor less than 1");
        }
        Calendar now = Calendar.getInstance();
        DateFormat date = new SimpleDateFormat("yyyy");
        Integer this_year = Integer.parseInt(date.format(now));
        Integer this_month = now.get(Calendar.MONTH) + 1;
        String regex = "^(";
        for(int i = 0; i < months; i++)
        {
            Integer month = this_month - i;
            Integer year = this_year;
            if(month <= 0)
            {
                month += 12;
                year -= 1;
            }
            regex += year.toString() + "-" + String.format("%02d", month);
            if(i != months - 1)
            {
                regex += "|";
            }
        }
        regex += ")";

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("date").regex(regex)),
            Aggregation.group("author").count().as("reviews"),
            Aggregation.sort(Sort.Direction.DESC, "reviews"),
            Aggregation.limit(10)
        );
        List<DBObject> user_dbos = mongoTemplate.aggregate(aggregation, "reviews", DBObject.class).getMappedResults();
        List<TopUserDTO> users = user_dbos.stream().map(user -> new TopUserDTO(user.get("_id").toString(), (Integer)user.get("reviews"))).toList();
        return users;
    }
}
