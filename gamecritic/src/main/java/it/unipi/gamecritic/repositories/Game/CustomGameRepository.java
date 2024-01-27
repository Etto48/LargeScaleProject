package it.unipi.gamecritic.repositories.Game;

import it.unipi.gamecritic.entities.Game;

import java.util.List;

public interface CustomGameRepository {
    //List<Game> findByName(String Name);
    List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
    List<Game> findLatest(Integer offset);
    List<Game> findBest(Integer offset);
    List<Game> findVideoGamesOfCompany(String companyName);
    List<Game> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    List<Game> search(String query);
    void deleteGame(String name);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
}
