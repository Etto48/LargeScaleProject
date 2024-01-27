package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.repositories.User.DTO.UserDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepositoryNeo4J extends Neo4jRepository<UserDTO, Long> {
    @Query("MATCH (follower:User {username: $followerUsername}), (following:User {username: $followedUsername}) " +
            "MERGE (follower)-[:FOLLOWS]->(followed); ")
    List<UserDTO> addFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );
}
