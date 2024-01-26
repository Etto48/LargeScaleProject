package it.unipi.gamecritic.repositories.Review;

import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Review.DTO.ReviewDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepositoryNeo4J extends Neo4jRepository<ReviewDTO, Long> {
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
}
