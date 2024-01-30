package it.unipi.gamecritic.controllers;

import java.util.List;
import java.util.Vector;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Company;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.user.CompanyManager;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Company.CompanyRepository;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyController {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    @Autowired
    public CompanyController(GameRepository gameRepository, CompanyRepository companyRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    @RequestMapping(value = "/company/{company}", method = RequestMethod.GET)
    public String company(
        @PathVariable(value = "company") String company,
        Model model,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        List<Company> company_list = companyRepository.findByDynamicAttribute("Name", company);
        if (company_list.size() == 0)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        Company company_object = company_list.get(0);
        Float avg_top_score = 0.0f;
        Integer games_with_score = 0;
        for (Document game : company_object.top_games)
        {
            Float score = Float.valueOf(game.get("user_review").toString());
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

        model.addAttribute("company", company_object);
        model.addAttribute("avg_top_score", avg_top_score);
        return "company";
    }

    private static int clamp_score(int value)
    {
        if (value < 1)
        {
            return 1;
        }
        else if (value > 10)
        {
            return 10;
        }
        else
        {
            return value;
        }
    }

    @RequestMapping(value = "/company/{company}/games", method = RequestMethod.GET)
    public String company_games(
        @PathVariable("company") String company,
        Model model,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);

        List<Game> games = gameRepository.findVideoGamesOfCompany(company);
        model.addAttribute("company_name", company);
        model.addAttribute("games", games);

        Float avg_score = 0.0f;
        Vector<Float> score_distribution = new Vector<Float>();
		for (int i = 0; i < 10; i++) {
			score_distribution.add(0f);
		}
        Integer games_with_score = 0;
		for (Game game : games) {
            if(game.customAttributes.get("user_review") == null)
            {
                continue;
            }
            Float score = Float.valueOf(game.customAttributes.get("user_review").toString());
            if(score != null)
            {
                avg_score += score;
                int low_index = clamp_score((int) Math.floor(score));
                int high_index = clamp_score((int) Math.ceil(score));
                float alpha = score - low_index;
                score_distribution.set(low_index - 1, score_distribution.get(low_index - 1) + (1 - alpha));
                score_distribution.set(high_index - 1, score_distribution.get(high_index - 1) + alpha);
                games_with_score++;
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
                String company_name = ((CompanyManager)user).company_name;
                List<Game> games = gameRepository.findVideoGamesOfCompany(company_name);
                model.addAttribute("games", games);
                return "company_panel";
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
    }
}
