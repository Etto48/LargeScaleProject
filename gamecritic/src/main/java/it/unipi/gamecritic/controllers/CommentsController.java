package it.unipi.gamecritic.controllers;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentsController {
    private final ReviewRepository reviewRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    public CommentsController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RequestMapping("/comments/{review_id}")
    public String comments(@PathVariable("review_id") String review_id, Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));
        
        try{
            Review review = reviewRepository.findSingleReview(Long.parseLong(review_id));
            if (review == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
            }
            model.addAttribute("review", review);
        }
        catch (NumberFormatException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found because of invalid id");
        }

        // TODO: get comments from database
        Vector<Comment> comments = new Vector<Comment>();
        comments.add(new Comment() {
            {
                id = 0;
                author = "Pippo";
                review_id = 0;
                quote = "Yeah pretty much";
                comments = new Vector<Comment>() {
                    {
                        add(new Comment() {
                            {
                                id = 1;
                                author = "Pluto";
                                review_id = 0;
                                quote = "I agree";
                                comments = new Vector<Comment>() {
                                    {
                                        add(new Comment() {
                                            {
                                                id = 2;
                                                author = "Paperino";
                                                review_id = 0;
                                                quote = "I disagree";
                                                comments = new Vector<Comment>();
                                            }
                                        });
                                        add(new Comment() {
                                            {
                                                id = 3;
                                                author = "Topolino";
                                                review_id = 0;
                                                quote = "I agree";
                                                comments = new Vector<Comment>();
                                            }
                                        });
                                    }
                                };
                            }
                        });
                        add(new Comment() {
                            {
                                id = 4;
                                author = "Paperino";
                                review_id = 0;
                                quote = "I am confused";
                                comments = new Vector<Comment>();
                            }
                        });
                    }
                };
            }
        });
        comments.add(new Comment() {
            {
                id = 5;
                author = "Pluto";
                review_id = 0;
                quote = "My opinion is different";
                comments = new Vector<Comment>();
            }
        });
        
        model.addAttribute("comments", comments);

        return "comments";
    }
}
