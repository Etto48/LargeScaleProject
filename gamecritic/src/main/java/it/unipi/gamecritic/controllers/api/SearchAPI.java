package it.unipi.gamecritic.controllers.api;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SearchAPI {
    @SuppressWarnings("unused")
    private final GameRepository gameRepository;
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    public class SearchResponse {
        public List<String> users;
        public List<String> games;
    }
    @Autowired
	public SearchAPI(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}
    @RequestMapping(value = "/api/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String search(
        @RequestParam(value = "query", required = true) String query,
        HttpServletRequest request, 
        HttpSession session) {


        // TODO: search for users and games in the database
        List<User> users = new Vector<>();
        List<Game> games = GameRepository.getMockupList();

        users.add(new User(
            "Pippo",
            "",
            "",
            new Vector<>()
        ));
        users.add(new User(
            "Pluto",
            "",
            "",
            new Vector<>()
        ));
        users.add(new User(
            "Paperino",
            "",
            "",
            new Vector<>()
        ));
        // filter results
        for (int i = users.size(); i > 0; i--) {
            if (!users.get(i - 1).username.toLowerCase().contains(query.toLowerCase())) {
                users.remove(i - 1);
            }
        }
        for (int i = games.size(); i > 0; i--) {
            if (!games.get(i - 1).name.toLowerCase().contains(query.toLowerCase())) {
                games.remove(i - 1);
            }
        }

        SearchResponse result = new SearchResponse();
        result.users = new Vector<>();
        result.games = new Vector<>();
        for (User user : users) {
            result.users.add(user.username);
        }
        for (Game game : games) {
            result.games.add(game.name);
        }

        //convert to json
        Gson gson = new GsonBuilder().create();
        return gson.toJson(result);
    }
}
