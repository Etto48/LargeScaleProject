package it.unipi.gamecritic.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;

import it.unipi.gamecritic.controllers.api.GameAPI;

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
	private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    @Id
    public String id;
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

    /*
    private static Map<String, Object> setCustomAttributes(DBObject dbObject) {
        Map<String, Object> hashMap = new HashMap<>();

        for (String key : dbObject.keySet()) {
            Object value = dbObject.get(key);

            if (value instanceof Document) {
                // If the value is a Document, convert it to a HashMap recursively
                Map<String, Object> nestedHashMap = convertDocumentToHashMap((Document) value);
                hashMap.put(key, nestedHashMap);
            } else {
                // If the value is not a Document, simply put it in the HashMap
                hashMap.put(key, value);
            }
        }

        return hashMap;
    }*/
    /*
    private static Map<String, Object> convertDocumentToHashMap(Document document) {
        Map<String, Object> hashMap = new HashMap<>();

        // Use reflection to access the fields of the Document class
        Field[] fields = document.getClass().getDeclaredFields();

        for (Field field : fields) {
            // Ensure private fields are accessible
            field.setAccessible(true);

            try {
                // Get the value of the field from the document
                Object value = field.get(document);

                // If the value is a nested Document, convert it to a HashMap recursively
                if (value instanceof Document) {
                    Map<String, Object> nestedHashMap = convertDocumentToHashMap((Document) value);
                    hashMap.put(field.getName(), nestedHashMap);
                } else {
                    // If the value is not a Document, simply put it in the HashMap
                    hashMap.put(field.getName(), value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle the exception as needed
            }
        }

        return hashMap;
    }
    }*/
    /*
    public String release;
    public Vector<String> publishers;
    public Vector<String> developers;
    public Vector<String> genres;
    public Vector<String> platforms;
    public String perspective;
    public String gameplay;
    public String setting;
    public String description;
    public Float user_score;
    public String img;
    public Vector<Review> top_reviews;

    public Game(
        Integer id,
        String name,
        Float user_score,
        String description
        ) {
        this.id = id;
        this.name = name;
        this.user_score = user_score;
        this.description = description;
    }

    public Game(
        Integer id, 
        String name, 
        String release, 
        Vector<String> publishers, 
        Vector<String> developers, 
        Vector<String> genres, 
        Vector<String> platforms,
        String perspective, 
        String gameplay, 
        String setting, 
        String description, 
        Float user_score, 
        String img, 
        Vector<Review> top_reviews
        ) {
        this.id = id;
        this.name = name;
        this.release = release;
        this.publishers = publishers;
        this.developers = developers;
        this.platforms = platforms;
        this.genres = genres;
        this.perspective = perspective;
        this.gameplay = gameplay;
        this.setting = setting;
        this.description = description;
        this.user_score = user_score;
        this.img = img;
        this.top_reviews = top_reviews;
    }

    public Game() {
    }*/
}
