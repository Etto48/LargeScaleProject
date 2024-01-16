package it.unipi.gamecritic.controllers;

import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.user.Company;
import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyController {
    @RequestMapping("/company_panel")
    public String company(
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
                model.addAttribute("company", (Company)user);
                model.addAttribute("user", user);
                model.addAttribute("request", request);
                Vector<Game> games = new Vector<>();
                //games.add(new Game(0,"The Legend of Zelda: Breath of the Wild", 9f, ""));
                //games.add(new Game(1,"Super Mario Odyssey", 9f, ""));
                //games.add(new Game(2,"Super Smash Bros. Ultimate", 9f, ""));
                //model.addAttribute("games", games);
                return "company_panel";
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
    }
}
