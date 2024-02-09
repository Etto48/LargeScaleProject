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
    @Field("Top3ReviewsByLikes")
    public List<Review> top_reviews;


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
        List<org.bson.Document> reviews_object = (List<org.bson.Document>)db.get("Top3ReviewsByLikes");
        this.top_reviews = reviews_object.stream().map(Review::new).toList();
        
        @SuppressWarnings("unchecked")
        HashMap<String,Object> released = (HashMap<String,Object>) customAttributes.get("Released");
        this.released = released.get("Release Date").toString();
    }

    public Game(String st) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new ObjectMapper().readValue(st, HashMap.class);
            customAttributes = map;
        } catch (Exception e) {
            logger.error("Error while setting custom attributes: " + e.getMessage());
        }
        this.name = customAttributes.get("Name").toString();

        @SuppressWarnings("unchecked")
        HashMap<String, Object> released = (HashMap<String, Object>) customAttributes.get("Released");
        this.released = released.get("Release Date").toString();
    }

    public static org.bson.Document documentFromJson(String json) throws IllegalArgumentException {
        org.bson.Document doc = org.bson.Document.parse(json);
        if (doc == null)
        {
            throw new IllegalArgumentException("Invalid JSON");
        }
        if (doc.containsKey("_id")) {
            doc.remove("_id");
        }
        if (!doc.containsKey("Name")) {
            throw new IllegalArgumentException("Name is a required field");
        }
        if (!doc.containsKey("Developers")) {
            doc.put("Developers", List.of());
        }
        if (!doc.containsKey("Publishers")) {
            doc.put("Publishers", List.of());
        }
        doc.put("reviews", List.of());
        doc.put("user_review",null);
        doc.put("reviewCount",0);
        doc.put("Top3ReviewsByLikes", List.of());
        return doc;
    }
}
