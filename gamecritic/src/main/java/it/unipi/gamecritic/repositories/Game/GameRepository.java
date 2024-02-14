package it.unipi.gamecritic.repositories.Game;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Game> findHottest(Integer offset)
    {
        return gameRepositoryMongoDB.findHottest(offset);
    }

    public List<Game> search(String query)
    {
        return gameRepositoryMongoDB.search(query);
    }

    @Async
    @Transactional
    public void addGame(Document game)
    {
        gameRepositoryMongoDB.addGame(game);
        gameRepositoryNeo4J.addGame(game.getString("Name"));
    }

    @Async
    public void editGame(Document game, String id)
    {
        gameRepositoryMongoDB.editGame(game, id);
    }

    @Async
    @Transactional
    public void deleteGame(String name)
    {
        gameRepositoryMongoDB.deleteGame(name);
        gameRepositoryNeo4J.deleteGame(name);
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
