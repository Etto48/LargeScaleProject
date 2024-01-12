package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Game;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class CustomGameRepositoryImpl implements CustomGameRepository {
    private final MongoTemplate mongoTemplate;

    public CustomGameRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue) {
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));

        return mongoTemplate.find(query, DBObject.class, "videogames");
    }
}

