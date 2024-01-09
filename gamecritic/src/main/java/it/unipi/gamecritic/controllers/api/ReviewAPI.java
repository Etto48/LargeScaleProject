package it.unipi.gamecritic.controllers.api;

import java.util.Vector;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewAPI {
    @RequestMapping(value = "/api/review/new", method = RequestMethod.POST)
    @ResponseBody
    public String new_review(
        @RequestParam(value="game", required=true) String game, 
        @RequestParam(value="score", required=true) Integer score, 
        @RequestParam(value="quote", required=true) String quote, 
        HttpServletRequest request, 
        HttpSession session) {
        
        it.unipi.gamecritic.entities.user.User user = (it.unipi.gamecritic.entities.user.User) session.getAttribute("user");
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

    @RequestMapping(value = "/api/review/get", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String get_review(
        @RequestParam(value="game", required=false) String game, 
        @RequestParam(value="user", required=false) String user,
        @RequestParam(value="page", required=true) Integer page,
        HttpServletRequest request, 
        HttpSession session) {

        if ((game == null && user == null) || (game != null && user != null)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "You must specify either a game or a user");
        }
        
        Vector<it.unipi.gamecritic.entities.Review> reviews = new Vector<>();
        if (game != null) {
            // TODO: get the reviews for this game from the database
            reviews.add(new it.unipi.gamecritic.entities.Review(0, 0, "The Legend of Zelda: Breath of the Wild", "Pippo", "2023-02-01", "The Legend of Zelda: Breath of the Wild introduces a vast open world for the first time in the series, which includes many landmarks from previous entries in the series. The game also features 120 Shrines of Trials, which can be found throughout the game's world and which can be explored in any order. The game also features dungeons called Divine Beasts and mini-bosses called \"Hinox\", \"Molduga\", and \"Lynel\". The game also features an \"chemistry engine\" which defines the physical properties of most objects and governs how they interact with the player and one another.", 9));
            reviews.add(new it.unipi.gamecritic.entities.Review(1, 0, "The Legend of Zelda: Breath of the Wild", "Pluto", "2023-02-01", "Where it takes mechanics from others in the industry, it improves upon them; where it introduces new ones, you slap your forehead in amazement that it hasn't been done before. Breath of the Wild is development done right, and damn near the best game you'll play all year.", 6));
            reviews.add(new it.unipi.gamecritic.entities.Review(2, 0, "The Legend of Zelda: Breath of the Wild", "Paperino", "2023-02-01", "The Legend of Zelda: Breath of the Wild is a landmark release for its franchise and Nintendo. It's the first time the series offers complete freedom to explore its entire world. It's also one of the first times that Nintendo has truly taken a \"modern\" approach to game design, with a large, open world and a focus on exploration. It's a game that feels like it was made by a team that was given the freedom to create the game they wanted to make, and the result is a game that feels truly special.", 3));
        }
        else {
            // TODO: get the reviews for this user from the database
            reviews.add(new it.unipi.gamecritic.entities.Review(0, 0, "The Legend of Zelda: Breath of the Wild", "Pippo", "2023-02-01", "The Legend of Zelda: Breath of the Wild introduces a vast open world for the first time in the series, which includes many landmarks from previous entries in the series. The game also features 120 Shrines of Trials, which can be found throughout the game's world and which can be explored in any order. The game also features dungeons called Divine Beasts and mini-bosses called \"Hinox\", \"Molduga\", and \"Lynel\". The game also features an \"chemistry engine\" which defines the physical properties of most objects and governs how they interact with the player and one another.", 9));
            reviews.add(new it.unipi.gamecritic.entities.Review(3, 2, "The Elder Scrolls V: Skyrim", "Pippo", "2023-02-01", "The Elder Scrolls V: Skyrim is an action role-playing video game developed by Bethesda Game Studios and published by Bethesda Softworks. It is the fifth main installment in The Elder Scrolls series, following The Elder Scrolls IV: Oblivion, and was released worldwide for Microsoft Windows, PlayStation 3, and Xbox 360 on November 11, 2011.", 2));
            reviews.add(new it.unipi.gamecritic.entities.Review(4, 3, "The Witcher 3: Wild Hunt", "Pippo", "2023-02-01", "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by Polish developer CD Projekt Red and is based on The Witcher series of fantasy novels by Andrzej Sapkowski. It is the sequel to the 2011 game The Witcher 2: Assassins of Kings and the third main installment in the The Witcher's video game series, played in an open world with a third-person perspective.", 6));
        }

        Gson gson = new Gson();
        return gson.toJson(reviews);
    }
}
