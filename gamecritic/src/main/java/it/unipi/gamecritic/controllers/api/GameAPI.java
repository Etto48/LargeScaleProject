package it.unipi.gamecritic.controllers.api;

import java.util.List;
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
import it.unipi.gamecritic.repositories.Game.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameAPI {
    private final GameRepository gameRepository;
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
        
        List<Game> games = gameRepository.findByDynamicAttribute("Name", name);
        if (!games.isEmpty())
        {
            Game game = games.get(0);
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
        if (kind.equals("hottest"))
        {
            List<Game> games = gameRepository.findHottest(offset);
            if (games.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            Gson gson = new Gson();
            return gson.toJson(games);
        }
        else if (kind.equals("newest"))
        {
            List<Game> games = gameRepository.findLatest(offset);
            if (games.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            Gson gson = new Gson();
            return gson.toJson(games);
        }
        else if (kind.equals("best"))
        {
            List<Game> games = gameRepository.findBest(offset);
            if (games.isEmpty()){
                logger.warn("No games found on \""+kind+"\" page "+page.toString());
            }
            Gson gson = new Gson();
            return gson.toJson(games);
        }
        
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kind not found");
    }
}
