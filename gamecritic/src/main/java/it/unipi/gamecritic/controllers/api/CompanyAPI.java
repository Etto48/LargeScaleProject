package it.unipi.gamecritic.controllers.api;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.Game.GameRepositoryNeo4J;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyAPI {
    private final GameRepository gameRepository;
    private final GameRepositoryNeo4J gameRepositoryNeo4J;

    @Autowired
    public CompanyAPI(GameRepository gameRepository, GameRepositoryNeo4J gameRepositoryNeo4J) {
        this.gameRepository = gameRepository;
        this.gameRepositoryNeo4J = gameRepositoryNeo4J;
    }

    private static final Logger logger = LoggerFactory.getLogger(CompanyAPI.class);
    @RequestMapping(value = "/api/company/edit-game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String company_edit_game(
        @RequestParam(value = "name", required = true) String name,
        @RequestParam(value = "description", required = true) String description,
        @RequestParam(value = "image", required = true) String image,
        @RequestParam(value = "publishers[]", required = true) String[] publishers,
        @RequestParam(value = "developers[]", required = true) String[] developers,
        @RequestParam(value = "genres[]", required = true) String[] genres,
        @RequestParam(value = "platforms[]", required = true) String[] platforms,
        @RequestParam(value = "release_date", required = true) String release_date,
        HttpServletRequest request,
        HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                // TODO: edit game
                logger.info("Edit game \"" + name + "\" by " + user.username + "\ndescription: " + description + "\nimage: " + image + "\nrelease date: " + release_date + "\nplatforms: " + Arrays.toString(platforms) + "\ngenres: " + Arrays.toString(genres) + "\ndevelopers: " + Arrays.toString(developers) + "\npublishers: " + Arrays.toString(publishers));
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }
    }

    @RequestMapping(value = "/api/company/publish-game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String company_publish_game(
        @RequestParam(value = "name", required = true) String name,
        @RequestParam(value = "description", required = true) String description,
        @RequestParam(value = "image", required = true) String image,
        @RequestParam(value = "publishers[]", required = true) String[] publishers,
        @RequestParam(value = "developers[]", required = true) String[] developers,
        @RequestParam(value = "genres[]", required = true) String[] genres,
        @RequestParam(value = "platforms[]", required = true) String[] platforms,
        @RequestParam(value = "release_date", required = true) String release_date,
        HttpServletRequest request,
        HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                // TODO: publish game
                logger.info("Publish game \"" + name + "\" by " + user.username + "\ndescription: " + description + "\nimage: " + image + "\nrelease date: " + release_date + "\nplatforms: " + Arrays.toString(platforms) + "\ngenres: " + Arrays.toString(genres) + "\ndevelopers: " + Arrays.toString(developers) + "\npublishers: " + Arrays.toString(publishers));
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }
    }

    @RequestMapping(value = "/api/company/delete-game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String company_delete_game(
        @RequestParam(value = "name", required = true) String name,
        HttpServletRequest request,
        HttpSession session) 
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                gameRepository.deleteGame(name);
                gameRepositoryNeo4J.deleteGame(name);
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        }
    }
}
