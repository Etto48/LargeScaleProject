package it.unipi.gamecritic.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LikeAPI {
    private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    @RequestMapping(value = "/api/like/set/review", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String like_post_review(
        @RequestParam(value = "id", required = true) Integer id,
        @RequestParam(value = "liked", required = true) Boolean liked,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: insert the like in the database
            logger.info("New like for review " + id + " from \"" + user.username + "\": " + liked);
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to like a review");
        }
    }

    @RequestMapping(value = "/api/like/set/game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String like_post_game(
        @RequestParam(value = "name", required = true) String name,
        @RequestParam(value = "liked", required = true) Boolean liked,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: insert the like in the database
            logger.info("New like for game \"" + name + "\" from \"" + user.username + "\": " + liked);
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to like a game");
        }
    }

    public class LikeInfo {
        public Boolean liked;
        public Integer likes;

        public LikeInfo(Boolean liked, Integer likes) {
            this.liked = liked;
            this.likes = likes;
        }
    }

    @RequestMapping(value = "/api/like/get/review", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String like_get_review(
        @RequestParam(value = "id", required = true) Integer id,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        LikeInfo info = null;
        if (user != null)
        {
            // TODO: get like info from database and check if the user liked the review
            info = new LikeInfo(true, 69);   
        }
        else
        {
            // TODO: get like info from database
            info = new LikeInfo(null, 69);
        }
        Gson gson = new Gson();
        return gson.toJson(info);
    }

    @RequestMapping(value = "/api/like/get/game", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String like_get_game(
        @RequestParam(value = "name", required = true) String name,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        LikeInfo info = null;
        if (user != null)
        {
            // TODO: get like info from database and check if the user liked the game
            info = new LikeInfo(true, 69);
        }
        else
        {
            // TODO: get like info from database
            info = new LikeInfo(null, 69);
        }
        Gson gson = new Gson();
        return gson.toJson(info);
    }
}
