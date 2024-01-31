package it.unipi.gamecritic.controllers.api;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.repositories.Game.GameRepository;
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
        @RequestParam(value = "game", required = true) String game,
        @RequestParam(value = "id", required = true) String id,
        HttpServletRequest request,
        HttpSession session) 
    {

        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                if (Util.checkCorrectCompany(game, user.getCompany_name())) {
                    gameRepository.editGame(game,id);
                    return "\"success\"";
                } else {
                    return "\"Your company is not a developer nor a publisher of this game\"";
                }
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
        @RequestParam(value = "game", required = true) String game,
        @RequestParam(value = "gameName", required = true) String gameName,
        HttpServletRequest request,
        HttpSession session) 
    {

        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                if (Util.checkCorrectCompany(game, user.getCompany_name())){
                    logger.info("go for insert");
                    gameRepository.addGame(game);
                    gameRepositoryNeo4J.addGame(gameName);
                    return "{}";
                }
                else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your company is not a developer nor a publisher of this game");
                }
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
