package it.unipi.gamecritic.repositories.Review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.entities.Review;

@Component
public class ReviewAsyncRepository {
    private final ReviewRepositoryNeo4J reviewRepositoryNeo4J;
    private static final Logger logger = LoggerFactory.getLogger(ReviewAsyncRepository.class);

    public ReviewAsyncRepository(ReviewRepositoryNeo4J reviewRepositoryNeo4J) {
        this.reviewRepositoryNeo4J = reviewRepositoryNeo4J;
    }
    
    @Async
    public void completeReviewInsertion(Review review, String id)
    {
        if(!Util.retryFor(() -> {
            reviewRepositoryNeo4J.insertReview(review.author, review.game, id, review.score);
        }))
        {
            logger.error("Error while inserting review into Neo4J (" + review.author + ", " + review.game + ", " + id + ", " + review.score + ")");
        }
        
    }
}
