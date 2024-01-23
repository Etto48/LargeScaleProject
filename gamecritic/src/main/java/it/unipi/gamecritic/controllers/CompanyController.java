package it.unipi.gamecritic.controllers;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.controllers.api.GameAPI;
import it.unipi.gamecritic.entities.Company;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.user.CompanyManager;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;

@Controller
public class CompanyController {
    @SuppressWarnings("unused")
    private final GameRepository gameRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    @Autowired
    public CompanyController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(value = "/company/{company}", method = RequestMethod.GET)
    public String company(
        @PathParam("company") String company,
        Model model,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        // TODO: find company in db   
        List<Game> games = GameRepository.getMockupList();
        Float avg_top_score = 0.0f;
        Integer games_with_score = 0;
        Vector<Game> top_games = new Vector<Game>();
        for (int i = 0; i < 3; i++) {
            top_games.add(games.get(i));
            Float score = (Float) games.get(i).customAttributes.get("user_score");
            if (score != null)
            {
                avg_top_score += score;
                games_with_score++;
            }    
        }
        if (games_with_score > 0)
        {
            avg_top_score /= games_with_score;
        }
        else
        {
            avg_top_score = null;
        }
        Company company_object = new Company("Nintendo Co., Ltd.", "Nintendo, founded originally by Fusajiro Yamauchi in Kyoto, Japan as Nintendo Koppai, has been around since 1889. The company originally sold high-quality Japanese playing cards (hanafuda) and later dabbled in toys and early electronic games. In the 1980's, Nintendo president Hiroshi Yamauchi wanted to change its direction. After seeking advice from his toy designers, they decided to turn towards developing arcade games. After the success of Donkey Kong in 1981, they decided to stay in the market. After its continuing success, they wanted to develop their own console, one that looked nice, and one that could be bought by the Japanese consumer for $99 US. This eventually went to $199, but it still sold very well. Nintendo went on to splitting its research and development into 4 distinct divisions, each of them were responsible for certain aspects of Nintendo's console. Nintendo is best known for creating Mario, Donkey Kong, Zelda, Metroid, and Pokémon series of games, all of them are million sellers. Nintendo is also known for engineering the Nintendo Entertainment System/Famicom, Super Nintendo/Super Famicom, Nintendo 64, Nintendo GameCube, Nintendo Wii, Nintendo Wii U, Game Boy, Game Boy Color, Game Boy Advance, Nintendo DS, Nintendo 3DS, Pokémon mini, and Virtual Boy.",
        "https://cdn.mobygames.com/08886c88-bc74-11ed-bde2-02420a000179.webp", top_games);
        model.addAttribute("company", company_object);
        model.addAttribute("avg_top_score", avg_top_score);
        return "company";
    }

    @RequestMapping(value = "/company/{company}/games", method = RequestMethod.GET)
    public String company_games(
        @PathParam("company") String company,
        Model model,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);

        // TODO: find games in db
        List<Game> games = GameRepository.getMockupList();
        model.addAttribute("company_name", company);
        model.addAttribute("avg_score", null);
        model.addAttribute("score_distribution", null);
        model.addAttribute("games", games);

        Float avg_score = 0.0f;
        Vector<Float> score_distribution = new Vector<Float>();
		for (int i = 0; i < 10; i++) {
			score_distribution.add(0f);
		}
        Integer games_with_score = 0;
		for (Game game : games) {
            Float score = (Float) game.customAttributes.get("user_score");
            if(score != null)
            {
                avg_score += score;
                Integer index = (int) Math.round(score) - 1;
                games_with_score++;
			    score_distribution.set(index, score_distribution.get(index) + 1);
            }
		}
        if(games_with_score > 0)
        {
            for (int i = 0; i < 10; i++) {
                score_distribution.set(i, score_distribution.get(i) / games_with_score * 100);
            }
            avg_score /= games_with_score;
        }
        else
        {
            score_distribution = null;
            avg_score = null;
        }
        model.addAttribute("avg_score", avg_score);
        model.addAttribute("score_distribution", score_distribution);
        return "company_games";
    }

    @RequestMapping(value = "/company_panel", method = RequestMethod.GET)
    public String company_panel(
        Model model,
        HttpServletRequest request,
        HttpSession session) 
    {
        if (session.getAttribute("user") == null) 
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        } else {
            User user = (User) session.getAttribute("user");
            if (user.getAccountType().equals("Company")) 
            {
                model.addAttribute("company", (CompanyManager)user);
                model.addAttribute("user", user);
                model.addAttribute("request", request);
                List<Game> games = GameRepository.getMockupList();
                model.addAttribute("games", games);
                return "company_panel";
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
    }
}
