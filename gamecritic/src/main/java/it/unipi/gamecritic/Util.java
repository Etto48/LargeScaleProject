package it.unipi.gamecritic;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static final int MAX_RETRIES = 5;
    private static final int SLEEP_TIME_MS = 1000;
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    public static boolean checkCorrectCompany(Document game, String company){
        try
        {
            List<?> publishers = game.get("Publishers", List.class);
            for (Object publisher : publishers){
                if (publisher != null && publisher.toString().equals(company)) return true;
            }
        }
        catch (ClassCastException e)
        {
            try 
            {
                String publishers = game.get("Publishers", String.class);
                if (publishers.equals(company)) return true;
            }
            catch (ClassCastException e2)
            {
                
            }
        }

        try
        {
            List<?> developers = game.get("Developers", List.class);
            for (Object developer : developers){
                if (developer != null && developer.toString().equals(company)) return true;
            }
        }
        catch (ClassCastException e)
        {
            try 
            {
                String publishers = game.get("Publishers", String.class);
                if (publishers.equals(company)) return true;
            }
            catch (ClassCastException e2)
            {
                
            }
        }

        return false;
    }

    public static boolean retryFor(Runnable runnable)
    {   
        for (int i = 0; i < MAX_RETRIES; i++)
        {
            try
            {
                runnable.run();
                return true;
            }
            catch (Exception e)
            {
                logger.warn("Retrying for the " + i + " time: " + e.getMessage());
                try
                {
                    Thread.sleep(SLEEP_TIME_MS);
                }
                catch (InterruptedException e2)
                {
                    return false;
                }
            }
        }
        return false;
    }
}
