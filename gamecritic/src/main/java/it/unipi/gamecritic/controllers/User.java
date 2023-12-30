package it.unipi.gamecritic.controllers;

import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Review;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class User {
    @RequestMapping("/user/{username}")
    public String user(@PathVariable(value="username") String username, Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));

        // TODO: get user from database
        Vector<it.unipi.gamecritic.entities.User> users = new Vector<it.unipi.gamecritic.entities.User>()
        {
            {
                add(new it.unipi.gamecritic.entities.User()
                {
                    {
                        username = "Pippo";
                        password_hash = "5f4dcc3b5aa765d61d8327deb882cf99";
                        email = "pippo@gmail.com";
                        top_reviews = new Vector<it.unipi.gamecritic.entities.Review>()
                        {
                            {
                                add(new it.unipi.gamecritic.entities.Review()
                                {
                                    {
                                        id = 0;
                                        game_id = 5;
                                        game = "Super Mario Bros";
                                        date = "2021-01-01";
                                        score = 10;
                                        author = "Pippo";
                                        quote = "This game is awesome!";
                                    }
                                });
                                add(new it.unipi.gamecritic.entities.Review()
                                {
                                    {
                                        id = 1;
                                        game_id = 6;
                                        game = "Super Mario Bros 2";
                                        date = "2021-01-01";
                                        score = 9;
                                        author = "Pippo";
                                        quote = "This game is awesome!";
                                    }
                                });
                                add(new it.unipi.gamecritic.entities.Review()
                                {
                                    {
                                        id = 2;
                                        game_id = 7;
                                        game = "Super Mario Bros 3";
                                        date = "2021-01-01";
                                        score = 8;
                                        author = "Pippo";
                                        quote = "This game is awesome!";
                                    }
                                });
                            }
                        };
                    }
                });
                add(new it.unipi.gamecritic.entities.User()
                {
                    {
                        username = "Pluto";
                        password_hash = "5f4dcc3b5aa765d61d8327deb882cf99";
                        email = "pluto@gmail.com";
                    }
                });
                add(new it.unipi.gamecritic.entities.User()
                {
                    {
                        username = "Paperino";
                        password_hash = "5f4dcc3b5aa765d61d8327deb882cf99";
                        email = "paperino@gmail.com";
                    }
                });
            }
        };
        for (it.unipi.gamecritic.entities.User user : users) {
            if (user.username.equals(username)) {
                model.addAttribute("viewed_user", user);
                Float avg_top_score = 0f;
                if (user.top_reviews != null)
                {
                    for (it.unipi.gamecritic.entities.Review review : user.top_reviews) {
                        avg_top_score += review.score;
                    }
                    avg_top_score /= user.top_reviews.size();
                    model.addAttribute("avg_top_score", avg_top_score);
                }
                return "user";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @RequestMapping("/user/{username}/reviews")
    public String user_reviews(@PathVariable(value="username") String username, Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("viewed_user_username", username);

        // TODO: get reviews from database
        Vector<Review> reviews = new Vector<Review>()
        {
            {
                add(new Review()
                {
                    {
                        id = 1;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 7;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 2;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 9;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 8;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 1;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 5;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 2;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 10;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 10;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 1;
                        game_id = 5;
                        game = "Super Mario Bros";
                        date = "2021-01-01";
                        score = 1;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 2;
                        game_id = 6;
                        game = "Super Mario Bros 2";
                        date = "2021-01-01";
                        score = 6;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
                add(new Review()
                {
                    {
                        id = 3;
                        game_id = 7;
                        game = "Super Mario Bros 3";
                        date = "2021-01-01";
                        score = 2;
                        author = "Pippo";
                        quote = "This game is awesome!";
                    }
                });
            }
        };
        model.addAttribute("reviews", reviews);
        Float avg_score = 0f;
        Vector<Float> distribution = new Vector<Float>();
        for (int i = 0; i < 10; i++) {
            distribution.add(0f);
        }
        for (Review review : reviews) {
            avg_score += review.score;
            distribution.set(review.score - 1, distribution.get(review.score - 1) + 1);
        }
        avg_score /= reviews.size();
        for (int i = 0; i < 10; i++) {
            distribution.set(i, distribution.get(i) / reviews.size() * 100);
        }
        model.addAttribute("avg_score", avg_score);
        model.addAttribute("score_distribution", distribution);
        return "user_reviews";
    }
}
