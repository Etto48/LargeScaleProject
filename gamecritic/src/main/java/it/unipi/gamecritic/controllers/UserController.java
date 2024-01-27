//UserController

package it.unipi.gamecritic.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import it.unipi.gamecritic.repositories.User.UserRepository;
import it.unipi.gamecritic.repositories.UserImage.UserImageRepository;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.entities.UserImage;
import it.unipi.gamecritic.entities.user.CompanyManager;
import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(ReviewRepository reviewRepository, UserRepository userRepository, UserImageRepository userImageRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
    }

    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public String user(
            @PathVariable(value="username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User u = (User) session.getAttribute("user");
        model.addAttribute("request", request);
        model.addAttribute("user", u);

        // get user from database
        List<User> users = userRepository.findByDynamicAttribute("username", username);
        for (User user : users) {
            if (user.username.equals(username)) {
                model.addAttribute("viewed_user", user);
                if(user.getAccountType() == "Company")
                {
                    model.addAttribute("company_name", ((CompanyManager)user).company_name);
                }

                Float avg_top_score = 0f;
                if (user.top_reviews != null)
                {
                    for (Review review : user.top_reviews) {
                        avg_top_score += review.score;
                    }
                    if (!user.top_reviews.isEmpty())
                    {
                        avg_top_score /= user.top_reviews.size();
                    }
                    else
                    {
                        avg_top_score = null;
                    }
                    model.addAttribute("avg_top_score", avg_top_score);
                }
                return "user";
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @RequestMapping(value = "/user/{username}/reviews", method = RequestMethod.GET)
    public String user_reviews(
            @PathVariable(value="username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        model.addAttribute("request", request);
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("viewed_user_username", username);

        List<Review> reviews = reviewRepository.findByAuthor(username);
        
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
        if(reviews.size() == 0)
        {
            avg_score = null;
        }
        else
        {
            avg_score /= reviews.size();
            for (int i = 0; i < 10; i++) {
                distribution.set(i, distribution.get(i) / reviews.size() * 100);
            }
        }
        model.addAttribute("avg_score", avg_score);
        model.addAttribute("score_distribution", distribution);
        return "user_reviews";
    }

    @RequestMapping(value = "/user/{username}/image.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] user_image(@PathVariable(value="username") String username) {
        UserImage image = userImageRepository.findImageByUsername(username);
        if (image == null)
        {
            try 
            {
                File file = ResourceUtils.getFile("classpath:static/img/missing_user_image.png");
                return FileUtils.readFileToByteArray(file);
            }
            catch (FileNotFoundException e)
            {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default user image not found");
            }
            catch (IOException e)
            {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while reading default user image");
            }
        }
        return image.getImage();
    }

    @RequestMapping(value = "/user/{username}/followers", method = RequestMethod.GET)
    public String user_followers(
            @PathVariable(value = "username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("viewed_user_username", username);

        // TODO: get followers from database
        Vector<User> followers = new Vector<User>();
        followers.add(new User("Pippo", null, null, null));
        followers.add(new User("Pluto", null, null, null));
        followers.add(new User("Paperino", null, null, null));

        model.addAttribute("follows", followers);
        model.addAttribute("mode", "followers");

        return "user_follow_list";
    }

    @RequestMapping(value = "/user/{username}/followed", method = RequestMethod.GET)
    public String user_followed(
            @PathVariable(value = "username") String username,
            Model model,
            HttpServletRequest request,
            HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("request", request);
        model.addAttribute("viewed_user_username", username);

        // TODO: get followed from database
        Vector<User> followed = new Vector<User>();
        followed.add(new User("Pippo", null, null, null));
        followed.add(new User("Pluto", null, null, null));
        followed.add(new User("Paperino", null, null, null));

        model.addAttribute("follows", followed);
        model.addAttribute("mode", "followed");

        return "user_follow_list";
    }
}
