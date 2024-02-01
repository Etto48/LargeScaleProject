package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Game.GameAsyncRepository;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final MongoTemplate mongoTemplate;
    private final GameAsyncRepository gameAsyncRepository; 
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CustomReviewRepositoryImpl.class);

    public CustomReviewRepositoryImpl(MongoTemplate mongoTemplate, GameAsyncRepository reviewAsyncRepository) {
        this.mongoTemplate = mongoTemplate;
        this.gameAsyncRepository = reviewAsyncRepository;
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
    public Review findSingleReview(String id)
    {
        if(id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try
        {
            ObjectId oid = new ObjectId(id);
            Query query = new Query(Criteria.where("_id").is(oid));
            return mongoTemplate.findOne(query, Review.class, "reviews");
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    @Override
    public String insertReview(Review review)
    {
        if(review == null) {
            throw new IllegalArgumentException("review must not be null");
        }
        
        try
        {
            String id = mongoTemplate.insert(review, "reviews").id.toHexString();
            gameAsyncRepository.completeReviewInsertion(review, id);
            return id;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    @Async
    public void deleteReview(String id)
    {
        if(id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try
        {
            ObjectId oid = new ObjectId(id);
            Query reviewsQuery = new Query(Criteria.where("_id").is(oid));
            Query commentsQuery = new Query(Criteria.where("reviewId").is(id));
            gameAsyncRepository.completeReviewDeletion(id);
            mongoTemplate.remove(reviewsQuery, Review.class, "reviews");
            mongoTemplate.remove(commentsQuery, Comment.class, "comments");
        }
        catch (IllegalArgumentException e)
        {
            return;
        }
    }
}
