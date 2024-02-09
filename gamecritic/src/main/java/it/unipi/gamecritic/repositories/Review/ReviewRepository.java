package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Review.DTO.ReviewDTO;

@Controller
public class ReviewRepository implements CustomReviewRepository {
    private final ReviewRepositoryMongoDB reviewRepositoryMongoDB;
    private final ReviewAsyncRepository reviewAsyncRepository;
    private final ReviewRepositoryNeo4J reviewRepositoryNeo4J;
    private static final Logger logger = LoggerFactory.getLogger(ReviewRepository.class);

    public ReviewRepository(
        ReviewRepositoryMongoDB reviewRepositoryMongoDB, 
        ReviewAsyncRepository reviewAsyncRepository,
        ReviewRepositoryNeo4J reviewRepositoryNeo4J) 
    {
        this.reviewRepositoryMongoDB = reviewRepositoryMongoDB;
        this.reviewAsyncRepository = reviewAsyncRepository;
        this.reviewRepositoryNeo4J = reviewRepositoryNeo4J;
    }


    @Override
    public List<Review> findByDynamicAttribute(String attributeName, String attributeValue) {
        return reviewRepositoryMongoDB.findByDynamicAttribute(attributeName, attributeValue);
    }

    @Override
    public List<Review> findByAuthor(String author) {
        return reviewRepositoryMongoDB.findByAuthor(author);
    }

    @Override
    public List<Review> findByGame(String game) {
        return reviewRepositoryMongoDB.findByGame(game);
    }

    @Override
    public Review findSingleReview(String id) {
        return reviewRepositoryMongoDB.findSingleReview(id);
    }

    @Override
    public String insertReview(Review review) {
        try
        {
            String id = reviewRepositoryMongoDB.insertReview(review);
            reviewAsyncRepository.completeReviewInsertion(review, id);
            return id;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Error while inserting review");
        }
    }

    @Override
    @Async
    public void deleteReview(String id) {
        try
        {
            reviewRepositoryMongoDB.deleteReview(id);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Review not found");
        }
        if(!Util.retryFor(() -> {
            reviewRepositoryNeo4J.deleteReview(id);
        }))
        {
            logger.error("Error while deleting review from Neo4J");
        }
    }

    @Override
    @Deprecated
    public void addLike(String id) {
        throw new UnsupportedOperationException("Use setLike(String id, String username, true) instead");
    }

    @Override
    @Deprecated
    public void removeLike(String id) {
        throw new UnsupportedOperationException("Use setLike(String id, String username, false) instead");
    }

    @Async
    public void setLike(String reviewId, String username, boolean like) {
        boolean ok = false;
        try
        {
            if(like)
            {   
                if(reviewRepositoryNeo4J.setLike(reviewId, username))
                {
                    ok = true;
                }
            }
            else
            {
                if(reviewRepositoryNeo4J.removeLike(reviewId, username))
                {
                    ok = true;
                }
            }
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Error while setting like");
        }
        if(ok)
        {
            if(!Util.retryFor(() -> {
                if (like)
                {
                    reviewRepositoryMongoDB.addLike(reviewId);
                }
                else
                {
                    reviewRepositoryMongoDB.removeLike(reviewId);
                }
            }));
        }
    }

    public Long getLikes(String reviewId)
    {
        return reviewRepositoryNeo4J.getLikes(reviewId);
    }

    public Boolean userLikedReview(String username,String reviewId)
    {
        return reviewRepositoryNeo4J.userLikedReview(username, reviewId);
    }

    public List<ReviewDTO> findMostLikedReviewsForUsers(String username)
    {
        return reviewRepositoryNeo4J.findMostLikedReviewsForUsers(username);
    }

    public List<ReviewDTO> findMostLikedReviewsForGames(String name)
    {
        return reviewRepositoryNeo4J.findMostLikedReviewsForGames(name);
    }
}
