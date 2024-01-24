package it.unipi.gamecritic.controllers.api;

import java.util.ArrayList;
import java.util.List;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.repositories.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameAPI {
    @SuppressWarnings("unused")
    private final GameRepository gameRepository;
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
	@Autowired
	public GameAPI(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}
    @RequestMapping(value = "/api/game", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String game(
        @RequestParam(value = "name", required = true) String name,
        HttpServletRequest request,
        HttpSession session) 
    {
        // TODO: get the game from the database
        List<Game> games = GameRepository.getMockupList();
        
        Game game = null;
        for (Game g : games)
        {
            if (g.name.equals(name))
            {
                game = g;
                break;
            }
        }
        if (game != null)
        {
            Gson gson = new Gson();
            return gson.toJson(game);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
    }

    @RequestMapping(value = "/api/top-games", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String top_games(
        @RequestParam(value = "page", required = true) Integer page,
        @RequestParam(value = "kind", required = true) String kind,
        HttpServletRequest request,
        HttpSession session)
    {
        
        Integer num_results = 10;
        Integer offset = page * num_results;
        logger.info("GameAPI request");
        if (kind.equals("hottest"))
        {
            logger.info("page" + page.toString());
            List<DBObject> l = gameRepository.findVideoGamesWithMostReviewsLastMonth(offset,"month");
            if (l.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            List<Game> g = new ArrayList<>();
            for (DBObject o : l) {
                Game ga = new Game(o);
                g.add(ga);
            }
            Gson gson = new Gson();
            return gson.toJson(g);
        }
        else if (kind.equals("newest"))
        {
            logger.info("page" + page.toString());
            List<DBObject> l = gameRepository.findLatest(offset);
            if (l.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            List<Game> g = new ArrayList<>();
            for (DBObject o : l) {
                Game ga = new Game(o);
                g.add(ga);
            }
            Gson gson = new Gson();
            return gson.toJson(g);
        }
        else if (kind.equals("best"))
        {
            logger.info("page" + page.toString());
            List<DBObject> l = gameRepository.findBest(offset);
            if (l.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            List<Game> g = new ArrayList<>();
            for (DBObject o : l) {
                Game ga = new Game(o);
                g.add(ga);
            }
            Gson gson = new Gson();
            return gson.toJson(g);
        }
        
        // TODO: get the top games from the database
        List<Game> games = GameRepository.getMockupList();

        Gson gson = new Gson();
        return gson.toJson(games);
    }
}
