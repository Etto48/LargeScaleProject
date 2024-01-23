package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;

import java.util.List;

public interface CustomGameRepository {
    //List<Game> findByName(String Name);

    List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue);

    List<DBObject> findLatest(Integer offset);
    List<DBObject> findBest(Integer offset);
    List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
}
