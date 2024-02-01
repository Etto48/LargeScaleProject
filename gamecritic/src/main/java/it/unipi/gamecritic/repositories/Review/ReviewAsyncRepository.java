package it.unipi.gamecritic.repositories.Review;

import java.util.Arrays;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MergeOperation.WhenDocumentsDontMatch;
import org.springframework.data.mongodb.core.aggregation.MergeOperation.WhenDocumentsMatch;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;

/**
 * This class exists only because Async does not work inside the same class
 */
public class ReviewAsyncRepository {
    private final MongoTemplate mongoTemplate;

    public ReviewAsyncRepository(MongoTemplate mongoTemplate) {
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
                new Document("reviewId", id).append("score", review.score).append("date", review.date))))))),
            Aggregation.merge().intoCollection("videogames").whenDocumentsMatch(WhenDocumentsMatch.replaceDocument()).whenDocumentsDontMatch(WhenDocumentsDontMatch.discardDocument()).build()          
        );

        mongoTemplate.aggregate(updateAggregation, Game.class, Game.class).getMappedResults();
    }
}
