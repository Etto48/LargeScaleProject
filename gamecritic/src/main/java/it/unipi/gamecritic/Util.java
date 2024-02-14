package it.unipi.gamecritic;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static boolean checkCorrectCompany(Document game, String company){
        try
        {

            List<?> publishers = game.get("Publishers", List.class);
            logger.info("class of publishers " +publishers.getClass().toString());
            for (Object publisher : publishers){
                logger.info("publishers: "+ publisher.toString());
                if (publisher != null && publisher.toString().equals(company)) return true;
            }
        }
        catch (ClassCastException e)
        {
            try 
            {
                String publishers = game.get("Publishers", String.class);
                logger.info("publishers: " + publishers.toString());
                if (publishers.equals(company)) return true;
            }
            catch (ClassCastException e2)
            {
                
            }
        }

        try
        {
            List<?> developers = game.get("Developers", List.class);
            logger.info("class of developers " + developers.getClass().toString());
            for (Object developer : developers){
                logger.info("developers: " + developer.toString());
                if (developer != null && developer.toString().equals(company)) return true;
            }
        }
        catch (ClassCastException e)
        {
            try 
            {
                String developers = game.get("Developers", String.class);
                logger.info("developers: " + developers.toString());
                if (developers.equals(company)) return true;
            }
            catch (ClassCastException e2)
            {
                
            }
        }

        return false;
    }
}
