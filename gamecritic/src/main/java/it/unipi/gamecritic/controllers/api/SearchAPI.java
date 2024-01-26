package it.unipi.gamecritic.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Company;

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
import it.unipi.gamecritic.repositories.Company.CompanyRepository;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SearchAPI {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    public class UserSearchResponse {
        public String username;
        public String account_type;
    }
    public class SearchResponse {
        public List<UserSearchResponse> users;
        public List<String> games;
        public List<String> companies;
    }
    @Autowired
	public SearchAPI(GameRepository gameRepository, CompanyRepository companyRepository, UserRepository userRepository) {
		this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
	}
    @RequestMapping(value = "/api/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String search(
        @RequestParam(value = "query", required = true) String query,
        HttpServletRequest request, 
        HttpSession session) {

        List<DBObject> l = gameRepository.search(query);
        List<Game> games = new ArrayList<>();
        for (DBObject o : l) {
            Game ga = new Game(o);
            games.add(ga);
        }
        List<Company> companies = companyRepository.search(query);
        List<User> users = userRepository.search(query);

        SearchResponse result = new SearchResponse();
        result.users = new Vector<>();
        result.games = new Vector<>();
        result.companies = new Vector<>();
        for (User user : users) {
            UserSearchResponse userSearchResponse = new UserSearchResponse();
            userSearchResponse.username = user.username;
            userSearchResponse.account_type = user.getAccountType();
            result.users.add(userSearchResponse);
        }
        for (Game game : games) {
            result.games.add(game.name);
        }
        for (Company company : companies) {
            result.companies.add(company.name);
        }

        //convert to json
        Gson gson = new GsonBuilder().create();
        return gson.toJson(result);
    }
}
