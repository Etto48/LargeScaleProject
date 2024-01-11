package it.unipi.gamecritic.controllers.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LikeAPI {
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
            info = new LikeInfo(false, 0);   
        }
        else
        {
            // TODO: get like info from database
            info = new LikeInfo(null, 0);
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
            info = new LikeInfo(false, 0);
        }
        else
        {
            // TODO: get like info from database
            info = new LikeInfo(null, 0);
        }
        Gson gson = new Gson();
        return gson.toJson(info);
    }
}
