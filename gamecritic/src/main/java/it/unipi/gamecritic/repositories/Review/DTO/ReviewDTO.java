package it.unipi.gamecritic.repositories.Review.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.repository.query.Query;

import java.math.BigInteger;
@Node("Review")
public class ReviewDTO {
    @Id
    @GeneratedValue
    public Long id;
    @Property("reviewId")
    public String reviewId;
    @Property("rating")
    public String rating;
    @Property("text")
    public String text;

    public ReviewDTO(Long id, String rating, String reviewId, String text) {
        this.id = id;
        this.reviewId = reviewId;
        this.rating = rating;
        this.text = text != null ? text : "No text available";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
