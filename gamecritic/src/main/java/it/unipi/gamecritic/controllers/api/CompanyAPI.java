package it.unipi.gamecritic.controllers.api;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.repositories.Game.GameRepository;

import org.bson.Document;
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

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyAPI {
    private final GameRepository gameRepository;

    @Autowired
    public CompanyAPI(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @SuppressWarnings("unused")
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
                try
                {
                    Document gameDoc = Game.documentFromJson(game);
                    if (Util.checkCorrectCompany(gameDoc, user.getCompany_name())) {
                        gameRepository.editGame(gameDoc,id);
                        return "\"success\"";
                    } else {
                        return "\"Your company is not a developer nor a publisher of this game\"";
                    }
                }
                catch (Exception e)
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game format");
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
        HttpServletRequest request,
        HttpSession session) 
    {

        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                try 
                {
                    Document gameDoc = Game.documentFromJson(game);
                    if (Util.checkCorrectCompany(gameDoc, user.getCompany_name())){
                    
                        gameRepository.addGame(gameDoc);
                        return "\"success\"";
                    }
                    else {
                        return "\"Your company is not a developer nor a publisher of this game\"";
                    }
                }
                catch (Exception e)
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid game format");
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
