package it.unipi.gamecritic.controllers;

import java.util.List;

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
import it.unipi.gamecritic.repositories.Comment.CommentRepository;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CommentsController {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CommentsController.class);

    @Autowired
    public CommentsController(CommentRepository commentRepository, ReviewRepository reviewRepository) {
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
    }

    @RequestMapping("/comments/{review_id}")
    public String comments(@PathVariable("review_id") String review_id, Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));
        
        Review review = reviewRepository.findSingleReview(review_id);
        if (review == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }
        model.addAttribute("review", review);

        List<Comment> comments = commentRepository.findByReviewId(review_id);
        if (comments == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comments not found");
        }
        model.addAttribute("comments", comments);

        return "comments";
    }
}
