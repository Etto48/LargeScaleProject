package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.unipi.gamecritic.entities.Review;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final MongoTemplate mongoTemplate;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CustomReviewRepositoryImpl.class);

    public CustomReviewRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Review> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public List<Review> findByAuthor(String author) {
        if(author == null) {
            throw new IllegalArgumentException("author must not be null");
        }
        Query query = new Query(Criteria.where("author").is(author));
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public List<Review> findByGame(String game) {
        if(game == null) {
            throw new IllegalArgumentException("game must not be null");
        }
        Query query = new Query(Criteria.where("game").is(game));
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public Review findSingleReview(Long id)
    {
        if(id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        String string_id = Long.toHexString(id);
        string_id = "0".repeat(24 - string_id.length()) + string_id;
        ObjectId oid = new ObjectId(string_id);
        Query query = new Query(Criteria.where("_id").is(oid));
        return mongoTemplate.findOne(query, Review.class, "reviews");
    }

    @Override
    public void insertReview(Review review)
    {
        if(review == null) {
            throw new IllegalArgumentException("review must not be null");
        }
        mongoTemplate.insert(review, "reviews");
    }
}
