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
    @Property("score")
    public String score;
    @Property("likeCount")
    public Integer likeCount;

    public ReviewDTO(Long id, String score, String reviewId, Integer likeCount) {
        this.id = id;
        this.reviewId = reviewId;
        this.score = score;
        this.likeCount = likeCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
