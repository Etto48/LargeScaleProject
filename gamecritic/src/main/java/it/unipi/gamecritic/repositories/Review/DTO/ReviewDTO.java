package it.unipi.gamecritic.repositories.Review.DTO;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Review")
public class ReviewDTO {

    @Id
    @GeneratedValue
    public UUID id;
    @Property("reviewId")
    public String reviewId;
    @Property("score")
    public String score;
    @Property("likeCount")
    public Integer likeCount;

    public ReviewDTO(UUID id, String score, String reviewId, Integer likeCount) {
        this.id = id;
        this.reviewId = reviewId;
        this.score = score;
        this.likeCount = likeCount;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
}
