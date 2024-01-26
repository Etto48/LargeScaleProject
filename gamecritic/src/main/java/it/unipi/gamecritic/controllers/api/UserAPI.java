package it.unipi.gamecritic.controllers.api;

import java.security.MessageDigest;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.UserImage;
import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.User.UserRepository;
import it.unipi.gamecritic.repositories.UserImage.UserImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAPI {
    @SuppressWarnings("unused")
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);

    @Autowired
    public UserAPI(UserRepository userRepository, UserImageRepository userImageRepository) {
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
    }

    @RequestMapping(value = "/api/user/follow", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String user_follow(
        @RequestParam(value = "username", required = true) String username,
        @RequestParam(value = "follow", required = true) boolean follow,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: follow user
            logger.info("Follow user \"" + username + "\" ("+follow+") by " + user.username);
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/user/follows", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String user_follows(
        @RequestParam(value = "username", required = true) String username,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: check if user follows username
            boolean follows = true;

            Gson gson = new Gson();
            return gson.toJson(follows);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/user/edit", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String user_edit(
        @RequestParam(value = "email", required = false) String email,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam(value = "image", required = false) MultipartFile image,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if (email != null)
            {
                // TODO: save email in the database
                logger.info("Email changed: " + email);
            }
            if (password != null)
            {
                try {
                    byte[] password_hash = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"));
                    String password_hash_b64 = java.util.Base64.getEncoder().encodeToString(password_hash);
                    // TODO: save password in the database
                    logger.info("Password changed: " + password_hash_b64);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing password");
                }
            }
            if (image != null)
            {
                String image_type = image.getContentType();
                if(image_type == null || !image_type.equals("image/png"))
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PNG images are supported");
                }
                if(image.getSize() > 1024*1024)
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image too large");
                }
                try {
                    byte[] image_bytes = image.getBytes();
                    UserImage user_image = new UserImage(user.username, image_bytes);
                    userImageRepository.insertImage(user_image);
                    logger.info("Image uploaded: " + image.getBytes().length/1024f + "KB");
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing image");
                }
            }
            return "{}";
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    public class SuggestionResponse
    {
        public Vector<User> users;
        public Vector<String> games;
        public SuggestionResponse(Vector<User> users, Vector<String> games)
        {
            this.users = users;
            this.games = games;
        }
    }

    @RequestMapping(value = "/api/user/suggest", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String user_suggest(
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            // TODO: get suggestions from db
            Vector<User> suggestions = new Vector<User>();
            suggestions.add(new User("Pippo", null, null, null));
            suggestions.add(new User("Pluto", null, null, null));
            suggestions.add(new User("Paperino", null, null, null));
            suggestions.add(new User("Topolino", null, null, null));

            SuggestionResponse response = new SuggestionResponse(suggestions, null);
            Gson gson = new Gson();
            return gson.toJson(response);
        }
        else
        {
            Gson gson = new Gson();
            return gson.toJson(new SuggestionResponse(null, null));
        }
    }
}
