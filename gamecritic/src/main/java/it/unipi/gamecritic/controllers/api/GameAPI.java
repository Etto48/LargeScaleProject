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
        
        //Integer num_results = 10;
        //Integer offset = page * num_results;

        if (kind.equals("hottest"))
        {

        }
        else if (kind.equals("newest"))
        {

        }
        else if (kind.equals("best"))
        {

        }
        
        // TODO: get the top games from the database
        List<Game> games = GameRepository.getMockupList();

        Gson gson = new Gson();
        return gson.toJson(games);
    }
}
