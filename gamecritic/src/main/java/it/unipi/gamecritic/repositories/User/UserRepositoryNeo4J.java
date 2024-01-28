package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.repositories.User.DTO.UserDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryNeo4J extends Neo4jRepository<UserDTO, UUID> {
    @Query(
        "MATCH (follower:User {username: $followerUsername}), (following:User {username: $followedUsername}) " +
        "MERGE (follower)-[:FOLLOWS]->(followed);")
    List<UserDTO> addFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "MATCH (follower:User {username: $followerUsername})-[r:FOLLOWS]->(following:User {username: $followedUsername}) " +
        "DELETE r;")
    List<UserDTO> removeFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "MATCH (follower:User {username: $followerUsername})-[r:FOLLOWS]->(following:User {username: $followedUsername}) " +
        "RETURN r;")
    List<UserDTO> getFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "match (u1:User {username: $username})-[:WROTE]->(r1:Review)-[:ABOUT]->(g:Game)\n"+
        "<-[:ABOUT]-(r2:Review)<-[:WROTE]-(u2:User)\n"+
        "where u2.username <> u1.username and not (u1)-[:FOLLOWS]->(u2)\n"+
        "with u2, r1, r2, sum(abs(r1.score-r2.score))/count(r1) as avgDelta, count(r1) as reviewCount\n"+
        "return u2\n"+
        "order by avgDelta asc, reviewCount desc limit 4")
    List<UserDTO> findSuggestedUsers(
        @Param("username") String username
    );

    @Query(
        "match (u:User {username: $username})\n"+
        "match (u)-[:WROTE]->(r:Review)\n"+
        "detach delete u, r"
    )
    @Async
    Void deleteUser(
        @Param("username") String username
    );
}
