package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;

import java.util.List;

public interface CustomGameRepository {
    //List<Game> findByName(String Name);

    List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue);
    //List<Game> findByDynamicAttribute(String attributeName, String attributeValue);
}
