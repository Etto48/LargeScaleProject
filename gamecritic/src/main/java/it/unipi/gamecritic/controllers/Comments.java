package it.unipi.gamecritic.controllers;

import java.util.Vector;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Review;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;

@Controller
public class Comments {
    @RequestMapping("/comments/{review_id}")
    public String comments(@PathParam("review_id") String review_id, Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));

        // TODO: get comments from database
        Vector<Comment> comments = new Vector<Comment>();
        comments.add(new Comment() {
            {
                author = "Pippo";
                review_id = 0;
                quote = "Yeah pretty much";
                comments = new Vector<Comment>() {
                    {
                        add(new Comment() {
                            {
                                author = "Pluto";
                                review_id = 0;
                                quote = "I agree";
                                comments = new Vector<Comment>() {
                                    {
                                        add(new Comment() {
                                            {
                                                author = "Paperino";
                                                review_id = 0;
                                                quote = "I disagree";
                                                comments = new Vector<Comment>();
                                            }
                                        });
                                        add(new Comment() {
                                            {
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
                author = "Pluto";
                review_id = 0;
                quote = "My opinion is different";
                comments = new Vector<Comment>();
            }
        });
        
        Review review = new Review();
        review.id = 0;
        review.game = "The Legend of Zelda: Breath of the Wild";
        review.date = "2021-01-01";
        review.author = "Pippo";
        review.quote = "The legend of Zelda: Breath of the Wild is a game that has been released in 2017 for the Nintendo Switch and the Wii U. It is an open world game that has been praised for its freedom of exploration and its physics engine. It is the first game in the series to feature voice acting.";
        review.score = 9;

        model.addAttribute("review", review);
        model.addAttribute("comments", comments);

        return "comments";
    }
}
