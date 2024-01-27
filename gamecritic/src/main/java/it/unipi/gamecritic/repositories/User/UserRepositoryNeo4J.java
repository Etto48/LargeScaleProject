package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.repositories.User.DTO.SuggestionDTO;
import it.unipi.gamecritic.repositories.User.DTO.UserDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryNeo4J extends Neo4jRepository<UserDTO, UUID> {
    @Query("MATCH (follower:User {username: $followerUsername}), (following:User {username: $followedUsername}) " +
            "MERGE (follower)-[:FOLLOWS]->(followed); ")
    List<UserDTO> addFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "match (u1:User {username: $username})-[:WROTE]->(r1:Review)-[:ABOUT]->(g:Game)\n"+
        "<-[:ABOUT]-(r2:Review)<-[:WROTE]-(u2:User)\n"+
        "return u2 as reccomendedUser, count(r1) as reviewCount, sum(abs(r1.score-r2.score))/count(r1) as avgDelta\n"+
        "order by avgDelta asc, reviewCount desc limit 4")
    List<SuggestionDTO> findSuggestedUsers(
        @Param("username") String username
    );
}
