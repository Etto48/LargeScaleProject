package it.unipi.gamecritic.controllers.api;

import java.util.Vector;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameAPI {
    @RequestMapping(value = "/api/topgames", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String top_games(
        @RequestParam(value = "page", required = true) Integer page,
        @RequestParam(value = "kind", required = true) String kind,
        HttpServletRequest request,
        HttpSession session) {
        
        Integer num_results = 10;
        Integer offset = page * num_results;

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
        Vector<it.unipi.gamecritic.entities.Game> games = new Vector<>();
        games.add(new it.unipi.gamecritic.entities.Game(0, "The Legend of Zelda: Breath of the Wild", null, null, null, null, null, null, null, "The Legend of Zelda: Breath of the Wild is an action-adventure game developed and published by Nintendo, released for the Nintendo Switch and Wii U consoles on March 3, 2017. Breath of the Wild is set at the end of the Zelda timeline; the player controls Link, who awakens from a hundred-year slumber to defeat Calamity Ganon before it can destroy the kingdom of Hyrule.", 9.5f, "https://upload.wikimedia.org/wikipedia/en/c/c6/The_Legend_of_Zelda_Breath_of_the_Wild.jpg", null));
        games.add(new it.unipi.gamecritic.entities.Game(1, "The Witcher 3: Wild Hunt", null, null, null, null, null, null, null, "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by Polish developer CD Projekt Red and is based on The Witcher series of fantasy novels by Andrzej Sapkowski. It is the sequel to the 2011 game The Witcher 2: Assassins of Kings and the third main installment in the The Witcher's video game series, played in an open world with a third-person perspective.", 6f, "https://upload.wikimedia.org/wikipedia/en/0/0c/Witcher_3_cover_art.jpg", null));
        games.add(new it.unipi.gamecritic.entities.Game(2, "The Elder Scrolls V: Skyrim", null, null, null, null, null, null, null, "The Elder Scrolls V: Skyrim is an action role-playing video game developed by Bethesda Game Studios and published by Bethesda Softworks. It is the fifth main installment in The Elder Scrolls series, following The Elder Scrolls IV: Oblivion, and was released worldwide for Microsoft Windows, PlayStation 3, and Xbox 360 on November 11, 2011.", 2.7f, "https://upload.wikimedia.org/wikipedia/en/1/15/The_Elder_Scrolls_V_Skyrim_cover.png", null));

        Gson gson = new Gson();
        return gson.toJson(games);
    }

}
