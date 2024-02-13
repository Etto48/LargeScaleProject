package it.unipi.gamecritic.repositories.Game;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.repositories.Game.DTO.TopGameDTO;

import java.util.List;

import org.bson.Document;

public interface CustomGameRepository {
    //List<Game> findByName(String Name);
    List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
    List<Game> findLatest(Integer offset);
    List<Game> findBest(Integer offset);
    List<Game> findVideoGamesOfCompany(String companyName);
    List<Game> findHottest(Integer offset);
    List<Game> search(String query);
    void addGame(Document game);
    void editGame(Document game, String id);
    void deleteGame(String name);
    List<TopGameDTO> topGamesByAverageScore(Integer months, String companyName, Integer limit);
    List<Float> companyScoreDistribution(String companyName);
    List<Float> globalScoreDistribution();
    void updateTop3ReviewsByLikes();
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
}
