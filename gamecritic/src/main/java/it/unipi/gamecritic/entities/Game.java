package it.unipi.gamecritic.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "videogames")
public class Game {
	private static final Logger logger = LoggerFactory.getLogger(Game.class);
    @Id
    public ObjectId id;
    @Field("Name")
    public String name;
    @Field("Released")
    public String released;

    @Field("customAttributes")
    public Map<String, Object> customAttributes = new HashMap<>();

    // other fields, getters, setters

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public void setCustomAttributes(DBObject db) {
        try {
            @SuppressWarnings("unchecked")
            Map<String,Object> map = new ObjectMapper().readValue(db.toString(), HashMap.class);
            customAttributes = map;
        }
        catch (Exception e){
            logger.error("Error while setting custom attributes: " + e.getMessage());
        }
    }

    // Used for testing
    public Game(String name, String release, Map<String,Object> additionalAttributes, List<Review> top_reviews) {
        this.name = name;
        this.released = release;
        HashMap<String, Object> tmp_additionalAttributes = new HashMap<>();
        for (Map.Entry<String, Object> entry : additionalAttributes.entrySet()) {
            tmp_additionalAttributes.put(entry.getKey(), entry.getValue());
        }
        tmp_additionalAttributes.put("top_reviews", top_reviews);
        this.customAttributes = tmp_additionalAttributes;
    }

    public Game (DBObject db){
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new ObjectMapper().readValue(db.toString(), HashMap.class);
            customAttributes = map;
        } catch (Exception e) {
            logger.error("Error while setting custom attributes: " + e.getMessage());
        }
        this.name = customAttributes.get("Name").toString();
        
        @SuppressWarnings("unchecked")
        HashMap<String,Object> released = (HashMap<String,Object>) customAttributes.get("Released");
        this.released = released.get("Release Date").toString();
    }
}
