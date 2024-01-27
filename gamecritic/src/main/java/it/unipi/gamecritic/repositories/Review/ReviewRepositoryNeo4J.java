package it.unipi.gamecritic.repositories.Review;

import it.unipi.gamecritic.repositories.Review.DTO.ReviewDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReviewRepositoryNeo4J extends Neo4jRepository<ReviewDTO, UUID> {
    @Query("MATCH (user:User {username: $username})-[:WROTE]->(review:Review)\n" +
            "OPTIONAL MATCH (review)<-[like:LIKED]-()\n" +
            "WITH review, COUNT(like) AS likeCount\n" +
            "ORDER BY likeCount DESC\n" +
            "LIMIT 3\n" +
            "RETURN review, likeCount;")
    List<ReviewDTO> findMostLikedReviewsForUsers(@Param("username") String username);
    @Query("MATCH (game:Game {name: $name})<-[:ABOUT]-(review:Review)\n" +
            "OPTIONAL MATCH (review)<-[like:LIKED]-()\n" +
            "WITH review, COUNT(like) AS likeCount\n" +
            "ORDER BY likeCount DESC\n" +
            "LIMIT 3\n" +
            "RETURN review, likeCount;")
    List<ReviewDTO> findMostLikedReviewsForGames(@Param("name") String name);
    @Query("MERGE (u:User {username: $author})\n" +
            "MERGE (g:Game {name: $gameName})\n" +
            "CREATE (u)-[:WROTE]->(r:Review {reviewId: $reviewId, score: $score})\n" +
            "CREATE (r)-[:ABOUT]->(g)\n" +
            "RETURN r;")
    ReviewDTO insertReview(@Param("username")String author,
                      @Param("gameName")String gameName,
                      @Param("reviewId")String reviewId,
                      @Param("score")String score);
}
