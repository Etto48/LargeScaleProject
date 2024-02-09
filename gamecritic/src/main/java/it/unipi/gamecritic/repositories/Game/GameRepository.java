package it.unipi.gamecritic.repositories.Game;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.mongodb.MongoWriteException;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.repositories.Game.DTO.GameDTO;
import it.unipi.gamecritic.repositories.Game.DTO.TopGameDTO;

@Component
public class GameRepository implements CustomGameRepository {
    private final GameRepositoryMongoDB gameRepositoryMongoDB;
    private final GameRepositoryNeo4J gameRepositoryNeo4J;
    private static final Logger logger = LoggerFactory.getLogger(GameRepository.class);

    public GameRepository(GameRepositoryMongoDB gameRepositoryMongoDB, GameRepositoryNeo4J gameRepositoryNeo4j) {
        this.gameRepositoryMongoDB = gameRepositoryMongoDB;
        this.gameRepositoryNeo4J = gameRepositoryNeo4j;
    }

    public List<Game> findByDynamicAttribute(String attributeName, String attributeValue)
    {
        return gameRepositoryMongoDB.findByDynamicAttribute(attributeName, attributeValue);
    }

    public List<Game> findLatest(Integer offset)
    {
        return gameRepositoryMongoDB.findLatest(offset);
    }

    public List<Game> findBest(Integer offset)
    {
        return gameRepositoryMongoDB.findBest(offset);
    }

    public List<Game> findVideoGamesOfCompany(String companyName)
    {
        return gameRepositoryMongoDB.findVideoGamesOfCompany(companyName);
    }

    public List<Game> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest)
    {
        return gameRepositoryMongoDB.findVideoGamesWithMostReviewsLastMonth(offset, latest);
    }

    public List<Game> search(String query)
    {
        return gameRepositoryMongoDB.search(query);
    }

    @Async
    public void addGame(Document game)
    {
        try
        {
            gameRepositoryMongoDB.addGame(game);
        }
        catch (MongoWriteException e)
        {
            throw new IllegalArgumentException("Game already exists");
        }
        if (!Util.retryFor(() -> {
            gameRepositoryNeo4J.addGame(game.getString("Name"));
        }))
        {
            logger.error("Neo4J add game failed");
        }
    }

    @Async
    public void editGame(Document game, String id)
    {
        gameRepositoryMongoDB.editGame(game, id);
    }

    @Async
    public void deleteGame(String name)
    {
        try
        {
            gameRepositoryMongoDB.deleteGame(name);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Game not found");
        }
        if (!Util.retryFor(() -> {
            gameRepositoryNeo4J.deleteGame(name);
        }))
        {
            logger.error("Neo4J delete game failed");
        }
    }

    public List<TopGameDTO> topGamesByAverageScore(Integer months, String companyName, Integer limit)
    {
        return gameRepositoryMongoDB.topGamesByAverageScore(months, companyName, limit);
    }

    public List<Float> companyScoreDistribution(String companyName)
    {
        return gameRepositoryMongoDB.companyScoreDistribution(companyName);
    }

    public List<Float> globalScoreDistribution()
    {
        return gameRepositoryMongoDB.globalScoreDistribution();
    }

    @Async
    public void updateTop3ReviewsByLikes()
    {
        try
        {
            gameRepositoryMongoDB.updateTop3ReviewsByLikes();
        }
        catch (Exception e)
        {
            logger.error("Error while updating top 3 reviews by likes: " + e.getMessage());
        }   
    }

    public List<GameDTO> findSuggestedGames(String username)
    {
        return gameRepositoryNeo4J.findSuggestedGames(username);
    }
}
