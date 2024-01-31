package it.unipi.gamecritic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
    public static boolean checkCorrectCompany(String game, String company){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = null;
        try {
            json = objectMapper.readTree(game);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(json.get("Publishers") != null)
        {
            if (json.get("Publishers").isArray()){
                for (JsonNode node : json.get("Publishers")){
                    if (node.asText().equals(company)) return true;
                }
            }
            else if (json.get("Publishers").asText().equals(company)) return true;
        }
        if(json.get("Developers") != null)
        {
            if (json.get("Developers").isArray()) {
                for (JsonNode node : json.get("Developers")) {
                    if (node.asText().equals(company)) return true;
                }
            } else if (json.get("Developers").asText().equals(company)) return true;
        }
        return false;
    }
}
