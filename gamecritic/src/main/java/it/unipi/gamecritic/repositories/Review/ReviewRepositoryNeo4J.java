package it.unipi.gamecritic.repositories.Review;

import it.unipi.gamecritic.repositories.Review.DTO.ReviewDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ReviewRepositoryNeo4J extends Neo4jRepository<ReviewDTO, UUID> {
    @Query(
        "MATCH (user:User {username: $username})-[:WROTE]->(review:Review)\n" +
        "OPTIONAL MATCH (review)<-[like:LIKED]-()\n" +
        "WITH review, COUNT(like) AS likeCount\n" +
        "ORDER BY likeCount DESC\n" +
        "LIMIT 3\n" +
        "RETURN review, likeCount;")
    List<ReviewDTO> findMostLikedReviewsForUsers(@Param("username") String username);
    @Query(
        "MATCH (game:Game {name: $name})<-[:ABOUT]-(review:Review)\n" +
        "OPTIONAL MATCH (review)<-[like:LIKED]-()\n" +
        "WITH review, COUNT(like) AS likeCount\n" +
        "ORDER BY likeCount DESC\n" +
        "LIMIT 3\n" +
        "RETURN review, likeCount;")
    List<ReviewDTO> findMostLikedReviewsForGames(@Param("name") String name);
    @Query(
        "MERGE (u:User {username: $author})\n" +
        "MERGE (g:Game {name: $gameName})\n" +
        "CREATE (u)-[:WROTE]->(r:Review {reviewId: $reviewId, score: $score})\n" +
        "CREATE (r)-[:ABOUT]->(g)"
    )
    @Async
    CompletableFuture<Void> insertReview(
        @Param("username")String author,
        @Param("gameName")String gameName,
        @Param("reviewId")String reviewId,
        @Param("score")Integer score);

    @Query(
        "MATCH (u:User {username: $username})-[l:LIKED]->(r:Review {reviewId: $reviewId})\n"+
        "RETURN count(l)=1;")
    Boolean userLikedReview(
        @Param("username")String username,
        @Param("reviewId")String reviewId);

    @Query(
        "MATCH (u:User)-[:LIKED]->(r:Review {reviewId: $reviewId})\n"+
        "RETURN count(u);")
    Long getLikes(@Param("reviewId")String reviewId);

    // set like for a user on a review
    // chatgpt edited this query to return a boolean
    @Query(
        "merge (u:User {username: $username})-[l:LIKED]->(r:Review {reviewId: $reviewId})\n"+
        "on create set l.created = true\n"+
        "on match set l.created = false\n"+
        "with l, l.created as created\n"+
        "remove l.created\n"+
        "return created"
    )
    Boolean setLike(
        @Param("username") String username,
        @Param("reviewId") String reviewId
    );

    // chatgpt edited this query to return a boolean
    @Query(
        "OPTIONAL MATCH (u:User {username: $username})-[l:LIKED]->(r:Review {reviewId: $reviewId})\n" +
        "DELETE l\n" +
        "RETURN CASE WHEN l is not null THEN true ELSE false END"
    )
    Boolean removeLike(
        @Param("username") String username,
        @Param("reviewId") String reviewId
    );

    @Query(
        "match (r:Review {reviewId: $reviewId})\n"+
        "detach delete r"
    )
    @Async
    CompletableFuture<Void> deleteReview(
        @Param("reviewId")String reviewId
    );
}
