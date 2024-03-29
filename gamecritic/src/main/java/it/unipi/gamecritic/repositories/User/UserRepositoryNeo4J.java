package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.repositories.User.DTO.TopUserDTO;
import it.unipi.gamecritic.repositories.User.DTO.UserDTO;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryNeo4J extends Neo4jRepository<UserDTO, UUID> {
    @Query(
        "MATCH (follower:User {username: $followerUsername})\n" +
        "MATCH (followed:User {username: $followedUsername})\n" +
        "MERGE (follower)-[:FOLLOWS]->(followed)"
    )
    Void addFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "MATCH (follower:User {username: $followerUsername})-[r:FOLLOWS]->(following:User {username: $followedUsername})\n" +
        "DELETE r"
    )
    Void removeFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "RETURN EXISTS ((:User {username: $followerUsername})-[:FOLLOWS]->(:User {username: $followedUsername}))"
    )
    Boolean getFollowRelationship(
            @Param("followerUsername") String followerUsername,
            @Param("followedUsername") String followedUsername
    );

    @Query(
        "MATCH (u:User {username: $username})\n"+
        "MATCH (u)-[:FOLLOWS]->(f:User)\n"+
        "RETURN f"
    )
    List<UserDTO> findFollowed(
        @Param("username") String username
    );

    @Query(
        "MATCH (u:User {username: $username})\n"+
        "MATCH (u)<-[:FOLLOWS]-(f:User)\n"+
        "RETURN f"
    )
    List<UserDTO> findFollowers(
        @Param("username") String username
    );

    @Query(
        "match (u1:User {username: $username})-[:WROTE]->(r1:Review)-[:ABOUT]->(g:Game)\n"+
        "<-[:ABOUT]-(r2:Review)<-[:WROTE]-(u2:User)\n"+
        "where u2.username <> u1.username and not (u1)-[:FOLLOWS]->(u2)\n"+
        "with u2, r1, r2, sum(abs(r1.score-r2.score))/count(r1) as avgDelta, count(r1) as reviewCount\n"+
        "return u2\n"+
        "order by avgDelta asc, reviewCount desc limit 4"
    )
    List<UserDTO> findSuggestedUsers(
        @Param("username") String username
    );

    @Query(
        "match (u:User {username: $username})\n"+
        "optional match (u)-[:WROTE]->(r:Review)\n"+
        "detach delete u, r"
    )
    Void deleteUser(
        @Param("username") String username
    );

    @Query(
        "match (u:User)-[:WROTE]->(r:Review)<-[l:LIKED]-(:User)\n"+
        "return u, count(l) as score\n"+
        "order by score desc\n"+
        "limit 10"
    )
    List<TopUserDTO> topUsersByLikes();

    @Query(
        "merge (u:User {username: $username})"
    )
    Void insertUserIfNotExists(
        @Param("username") String username
    );
}
