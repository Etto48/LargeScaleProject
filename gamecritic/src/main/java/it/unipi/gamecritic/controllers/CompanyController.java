package it.unipi.gamecritic.controllers;

import java.util.List;

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
        model.addAttribute("company", null);
        return "company";
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
