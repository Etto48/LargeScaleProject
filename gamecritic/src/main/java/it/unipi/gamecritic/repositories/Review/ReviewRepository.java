package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Review.DTO.ReviewDTO;

@Controller
public class ReviewRepository implements CustomReviewRepository {
    private final ReviewRepositoryMongoDB reviewRepositoryMongoDB;
    private final ReviewRepositoryNeo4J reviewRepositoryNeo4J;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ReviewRepository.class);

    public ReviewRepository(
        ReviewRepositoryMongoDB reviewRepositoryMongoDB, 
        ReviewRepositoryNeo4J reviewRepositoryNeo4J) 
    {
        this.reviewRepositoryMongoDB = reviewRepositoryMongoDB;
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
    @Transactional
    public String insertReview(Review review)
    {
        String id = reviewRepositoryMongoDB.insertReview(review);
        reviewRepositoryNeo4J.insertReview(review.author, review.game, id, review.score);
        return id;
    }

    @Override
    @Async
    @Transactional
    public void deleteReview(String id)
    {
        reviewRepositoryMongoDB.deleteReview(id);
        reviewRepositoryNeo4J.deleteReview(id);
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
    @Transactional
    public void setLike(String reviewId, String username, boolean like)
    {
        if(like)
        {   
            if(reviewRepositoryNeo4J.setLike(reviewId, username))
            {
                reviewRepositoryMongoDB.addLike(reviewId);
            }
        }
        else
        {
            if(reviewRepositoryNeo4J.removeLike(reviewId, username))
            {
                reviewRepositoryMongoDB.removeLike(reviewId);
            }
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
