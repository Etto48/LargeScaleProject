package it.unipi.gamecritic.repositories.Game.DTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameDTOMongo {
    private static final Logger logger = LoggerFactory.getLogger(it.unipi.gamecritic.entities.Game.class);
    public String name;
    public HashMap<String,String> released = new HashMap<>();
    public Map<String, Object> customAttributes = new HashMap<>();

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public void setCustomAttributes(DBObject db) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new ObjectMapper().readValue(db.toString(), HashMap.class);
            customAttributes = map;
        } catch (Exception e) {
            logger.error("Error while setting custom attributes: " + e.getMessage());
        }
    }

    // Used for testing


    public GameDTOMongo(String st) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new ObjectMapper().readValue(st, HashMap.class);
            customAttributes = map;
        } catch (Exception e) {
            logger.error("Error while setting custom attributes: " + e.getMessage());
        }
        this.name = customAttributes.get("Name").toString();
        this.released.put("Release Date",customAttributes.get("ReleaseDate").toString());
        this.released.put("Platform",customAttributes.get("Platform").toString());
    }
}
