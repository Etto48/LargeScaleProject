package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.unipi.gamecritic.entities.Review;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final MongoTemplate mongoTemplate;

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
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where("author").is(author));
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public List<Review> findByGame(String game) {
        if(game == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where("game").is(game));
        return mongoTemplate.find(query, Review.class, "reviews");
    }
}
