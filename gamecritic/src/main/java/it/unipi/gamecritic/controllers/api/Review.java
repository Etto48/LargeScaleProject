package it.unipi.gamecritic.controllers.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Review {
    @RequestMapping(value = "/api/review")
    @ResponseBody
    public String review(
        @RequestParam(value="game", required=true) String game, 
        @RequestParam(value="score", required=true) Integer score, 
        @RequestParam(value="quote", required=true) String quote, 
        HttpServletRequest request, 
        HttpSession session) {
        
        it.unipi.gamecritic.entities.User user = (it.unipi.gamecritic.entities.User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "You must be logged in to review a game");
        }
        else
        {
            // TODO: insert the review in the database
            System.out.println("New review for \"" + game + "\" from \"" + user.username + "\":\nscore: " + score + "\nquote: \"" + quote + "\"");

            return "success";
        }
    }
}
