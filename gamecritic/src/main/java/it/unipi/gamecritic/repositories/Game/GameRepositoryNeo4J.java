package it.unipi.gamecritic.repositories.Game;

import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import it.unipi.gamecritic.repositories.Game.DTO.GameDTO;

public interface GameRepositoryNeo4J extends Neo4jRepository<GameDTO, UUID> {
    @Query(
        "match (u1:User {username: $username})-[:FOLLOWS]->(u2:User)-[:WROTE]->(r:Review)-[:ABOUT]->(g:Game)\n"+
        "where not (u1)-[:WROTE]->(:Review)-[:ABOUT]->(g)\n"+
        "with g,r,u2, sum(r.score)/count(u2) as avgScore, count(r) as reviewCount\n"+
        "return g\n"+
        "order by avgScore desc, reviewCount desc limit 4"
    )
    List<GameDTO> findSuggestedGames(
        @Param("username") String username
    );

    @Query(
        "match (g:Game {name: $name})\n"+
        "match (g)<-[:ABOUT]-(r:Review)\n"+
        "detach delete g, r"
    )
    @Async
    Void deleteGame(
        @Param("name") String name
    );
}
