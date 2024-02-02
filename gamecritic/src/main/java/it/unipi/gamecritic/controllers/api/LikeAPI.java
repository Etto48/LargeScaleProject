package it.unipi.gamecritic.controllers.api;

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

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import it.unipi.gamecritic.repositories.Review.ReviewRepositoryNeo4J;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LikeAPI {
    private final ReviewRepository reviewRepository;
    private final ReviewRepositoryNeo4J reviewRepositoryNeo4J;

    @Autowired
    public LikeAPI(
        ReviewRepository reviewRepository,
        ReviewRepositoryNeo4J reviewRepositoryNeo4J) 
    {
        this.reviewRepository = reviewRepository;
        this.reviewRepositoryNeo4J = reviewRepositoryNeo4J;
    }

    private static final Logger logger = LoggerFactory.getLogger(LikeAPI.class);
    @RequestMapping(value = "/api/like/set/review", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String like_set_review(
        @RequestParam(value = "id", required = true) String id,
        @RequestParam(value = "liked", required = true) Boolean liked,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if (liked)
            {
                if(reviewRepositoryNeo4J.setLike(user.username, id))
                { 
                    reviewRepository.addLike(id);
                }
                else
                {
                    logger.warn("User " + user.username + " already liked review " + id);
                }
            }
            else
            {
                if(reviewRepositoryNeo4J.removeLike(user.username, id))
                {   
                    reviewRepository.removeLike(id);
                }
                else
                {
                    logger.warn("User " + user.username + " already unliked review " + id);
                }
            }
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to like a review");
        }
    }

    public class LikeInfo {
        public Boolean liked;
        public Long likes;

        public LikeInfo(Boolean liked, Long likes) {
            this.liked = liked;
            this.likes = likes;
        }
    }

    @RequestMapping(value = "/api/like/get/review", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String like_get_review(
        @RequestParam(value = "id", required = true) String id,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        LikeInfo info = null;
        long likes = reviewRepositoryNeo4J.getLikes(id);
        Boolean liked = null;
        if (user != null)
        {
            liked = reviewRepositoryNeo4J.userLikedReview(user.username, id);
        }
        info = new LikeInfo(liked, likes);
        Gson gson = new Gson();
        return gson.toJson(info);
    }
}
