package it.unipi.gamecritic.repositories.Game;

import java.util.Arrays;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MergeOperation.WhenDocumentsDontMatch;
import org.springframework.data.mongodb.core.aggregation.MergeOperation.WhenDocumentsMatch;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

import it.unipi.gamecritic.entities.Review;

/**
 * This class exists only because Async does not work inside the same class
 */
@Component
public class GameAsyncRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public GameAsyncRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }    

    @Async
    public void completeReviewInsertion(Review review, String id)
    {
        Aggregation updateAggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("name").is(review.game)),
            Aggregation.stage(new Document("$set", 
                new Document("user_review", 
                new Document("$divide", Arrays.asList(new Document("$add", Arrays.asList(new Document("$multiply", Arrays.asList("$reviewCount", "$user_review")), 4L)), 
                                new Document("$add", Arrays.asList("$reviewCount", 1L)))))
                        .append("reviewCount", 
                new Document("$add", Arrays.asList("$reviewCount", 1L)))
                        .append("reviews", 
                new Document("$concatArrays", Arrays.asList("$reviews", Arrays.asList(
                    new Document("reviewId", id).append("score", review.score).append("date", review.date).append("author", review.author))))))),
            Aggregation.merge()
                .intoCollection("videogames")
                .whenDocumentsMatch(WhenDocumentsMatch.replaceDocument())
                .whenDocumentsDontMatch(WhenDocumentsDontMatch.discardDocument())
                .build()          
        );

        mongoTemplate.aggregate(updateAggregation, DBObject.class, DBObject.class).getMappedResults();
    }

    @Async
    public void completeReviewDeletion(String review_id)
    {
        Aggregation updateAggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("reviews").elemMatch(Criteria.where("reviewId").is(review_id))),
            Aggregation.stage(new Document("$set", 
                new Document("user_review", 
                new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList(new Document("$multiply", Arrays.asList("$reviewCount", "$user_review")), 
                                        new Document("$getField", 
                                        new Document("input", 
                                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter", 
                                                        new Document("input", "$reviews")
                                                                .append("as", "review")
                                                                .append("cond", 
                                                        new Document("$not", 
                                                        new Document("$eq", Arrays.asList("$$review.reviewId", review_id))))), 0L)))
                                                .append("field", "score")))), 
                                new Document("$subtract", Arrays.asList("$reviewCount", 1L)))))
                        .append("reviewCount", 
                new Document("$subtract", Arrays.asList("$reviewCount", 1L)))
                        .append("reviews", 
                new Document("$filter", 
                new Document("input", "$reviews")
                                .append("as", "review")
                                .append("cond", 
                new Document("$not", 
                new Document("$eq", Arrays.asList("$$review.reviewId", review_id)))))))), 
            Aggregation.merge()
                .intoCollection("videogames")
                .whenDocumentsMatch(WhenDocumentsMatch.replaceDocument())
                .whenDocumentsDontMatch(WhenDocumentsDontMatch.discardDocument())
                .build()
        );

        mongoTemplate.aggregate(updateAggregation, DBObject.class, DBObject.class).getMappedResults();
    }

    @Async
    public void completeReviewDeletionForUser(String username)
    {
        Aggregation updateAggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("reviews").elemMatch(Criteria.where("author").is(username))),
            Aggregation.stage(new Document("$set", 
                new Document("user_review", 
                new Document("$divide", Arrays.asList(new Document("$subtract", Arrays.asList(new Document("$multiply", Arrays.asList("$reviewCount", "$user_review")), 
                                        new Document("$getField", 
                                        new Document("input", 
                                        new Document("$arrayElemAt", Arrays.asList(new Document("$filter", 
                                                        new Document("input", "$reviews")
                                                                .append("as", "review")
                                                                .append("cond", 
                                                        new Document("$not", 
                                                        new Document("$eq", Arrays.asList("$$review.author", username))))), 0L)))
                                                .append("field", "score")))), 
                                new Document("$subtract", Arrays.asList("$reviewCount", 1L)))))
                        .append("reviewCount", 
                new Document("$subtract", Arrays.asList("$reviewCount", 1L)))
                        .append("reviews", 
                new Document("$filter", 
                new Document("input", "$reviews")
                                .append("as", "review")
                                .append("cond", 
                new Document("$not", 
                new Document("$eq", Arrays.asList("$$review.author", username)))))))), 
            Aggregation.merge()
                .intoCollection("videogames")
                .whenDocumentsMatch(WhenDocumentsMatch.replaceDocument())
                .whenDocumentsDontMatch(WhenDocumentsDontMatch.discardDocument())
                .build()
        );

        mongoTemplate.aggregate(updateAggregation, DBObject.class, DBObject.class).getMappedResults();
    }
}