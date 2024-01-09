package it.unipi.gamecritic.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CompanyAPI {
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
        it.unipi.gamecritic.entities.user.User user = (it.unipi.gamecritic.entities.user.User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                // TODO: edit game
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not logged in");
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
        it.unipi.gamecritic.entities.user.User user = (it.unipi.gamecritic.entities.user.User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                // TODO: publish game
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not logged in");
        }
    }

    @RequestMapping(value = "/api/company/delete-game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String company_delete_game(
        @RequestParam(value = "name", required = true) String name,
        HttpServletRequest request,
        HttpSession session) 
    {
        it.unipi.gamecritic.entities.user.User user = (it.unipi.gamecritic.entities.user.User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Company"))
            {
                // TODO: delete game
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a company");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not logged in");
        }
    }
}
