package it.unipi.gamecritic.repositories.Review;

import java.util.List;

import it.unipi.gamecritic.entities.Review;

public interface CustomReviewRepository {
    List<Review> findByDynamicAttribute(String attributeName, String attributeValue);
    List<Review> findByAuthor(String author);
    List<Review> findByGame(String game);
    Review findSingleReview(String id);
    String insertReview(Review review);
}
