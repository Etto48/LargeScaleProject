package it.unipi.gamecritic.controllers.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReviewAPI {
    private final ReviewRepository reviewRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ReviewAPI.class);

    @Autowired
    public ReviewAPI(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RequestMapping(value = "/api/review/new", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String new_review(
        @RequestParam(value="game", required=true) String game, 
        @RequestParam(value="score", required=true) Integer score, 
        @RequestParam(value="quote", required=true) String quote, 
        HttpServletRequest request, 
        HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "You must be logged in to review a game");
        }
        else
        {
            Review review = new Review(game, score, quote, user.username, null);
            reviewRepository.insertReview(review);
            return "{}";
        }
    }
}
