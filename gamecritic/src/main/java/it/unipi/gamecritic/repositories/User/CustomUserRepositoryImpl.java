//CustomUserRepositoryImpl

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.entities.UserImage;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Game.GameAsyncRepository;
import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;

import com.mongodb.DBObject;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final MongoTemplate mongoTemplate;
    private final GameAsyncRepository gameAsyncRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserRepositoryImpl.class);

    public CustomUserRepositoryImpl(MongoTemplate mongoTemplate, GameAsyncRepository gameAsyncRepository) {
        this.mongoTemplate = mongoTemplate;
        this.gameAsyncRepository = gameAsyncRepository;
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
        gameAsyncRepository.completeReviewDeletionForUser(username);
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
        String escapedQuery = Pattern.quote(query);
        if (escapedQuery == null) {
            throw new IllegalArgumentException("The given query must not be null");
        }
        Criteria criteria = Criteria.where("username").regex(escapedQuery, "i");
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
        Calendar d = Calendar.getInstance();
        Integer this_year = d.get(Calendar.YEAR);
        Integer this_month = d.get(Calendar.MONTH) + 1;
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

    @Override
    @Async
    public void updateTop3ReviewsByLikes() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.stage(
                "{\n" + //
                "  $group: {\n" + //
                "    _id: \"$author\",\n" + //
                "    Top3ReviewsByLikes: {\n" + //
                "      $topN: {\n" + //
                "        n: 3,\n" + //
                "        output: {\n" + //
                "          _id: \"$_id\",\n" + //
                "          game: \"$game\",\n" + //
                "          quote: \"$quote\",\n" + //
                "          author: \"$author\",\n" + //
                "          date: \"$date\",\n" + //
                "          score: \"$score\",\n" + //
                "          likes: \"$likes\",\n" + //
                "        },\n" + //
                "        sortBy: {\n" + //
                "          likes: -1,\n" + //
                "        },\n" + //
                "      },\n" + //
                "    },\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $set: {\n" + //
                "    username: \"$_id\",\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $project: {\n" + //
                "    _id: 0,\n" + //
                "    username: 1,\n" + //
                "    Top3ReviewsByLikes: 1,\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $merge: {\n" + //
                "    into: \"users\",\n" + //
                "    on: \"username\",\n" + //
                "    whenMatched: \"merge\",\n" + //
                "    whenNotMatched: \"discard\",\n" + //
                "  },\n" + //
                "}"
            )
        ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        Instant start = Instant.now();
        mongoTemplate.aggregate(aggregation, "reviews", DBObject.class).getMappedResults();
        logger.info("Finished updating top 3 reviews by likes for all users in " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + " ms");
    }
}
