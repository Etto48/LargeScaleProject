package it.unipi.gamecritic.controllers.api;

import java.util.List;
import java.util.Vector;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Search {
    
    public class SearchResponse {
        public List<it.unipi.gamecritic.entities.User> users;
        public List<it.unipi.gamecritic.entities.Game> games;
    }

    @RequestMapping(value = "/api/search", produces = "application/json")
    @ResponseBody
    public String search(
        @RequestParam(value = "query", required = true) String query,
        HttpServletRequest request, 
        HttpSession session) {


        // TODO: search for users and games in the database
        List<it.unipi.gamecritic.entities.User> users = new Vector<>();
        List<it.unipi.gamecritic.entities.Game> games = new Vector<>();

        users.add(new it.unipi.gamecritic.entities.User(
            "Pippo",
            "",
            "",
            new Vector<>()
        ));
        users.add(new it.unipi.gamecritic.entities.User(
            "Pluto",
            "",
            "",
            new Vector<>()
        ));
        users.add(new it.unipi.gamecritic.entities.User(
            "Paperino",
            "",
            "",
            new Vector<>()
        ));

        games.add(new it.unipi.gamecritic.entities.Game(
            0,
            "The Legend of Zelda: Breath of the Wild",
            9.5f,
            "The Legend of Zelda: Breath of the Wild is a 2017 action-adventure game developed and published by Nintendo for the Nintendo Switch and Wii U consoles. Breath of the Wild is part of the Legend of Zelda franchise and is set at the end of the series' timeline; the player controls Link, who awakens from a hundred-year slumber to defeat Calamity Ganon before it can destroy the kingdom of Hyrule."
        ));

        games.add(new it.unipi.gamecritic.entities.Game(
            1,
            "The Witcher 3: Wild Hunt",
            9.5f,
            "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by Polish developer CD Projekt Red and is based on The Witcher series of fantasy novels written by Andrzej Sapkowski. It is the sequel to the 2011 game The Witcher 2: Assassins of Kings and the third main installment in the The Witcher's video game series, played in an open world with a third-person perspective."
        ));

        games.add(new it.unipi.gamecritic.entities.Game(
            2,
            "The Last of Us Part II",
            9.5f,
            "The Last of Us Part II is a 2020 action-adventure game developed by Naughty Dog and published by Sony Interactive Entertainment for the PlayStation 4. Set five years after The Last of Us (2013), the game focuses on two playable characters in a post-apocalyptic United States whose lives intertwine: Ellie, who sets out for revenge after suffering a tragedy, and Abby, a soldier who becomes involved in a conflict with a cult."
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
        result.users = users;
        result.games = games;

        //convert to json
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(result);

        return json;
    }
}
