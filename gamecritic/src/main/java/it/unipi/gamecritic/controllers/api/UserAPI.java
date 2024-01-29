package it.unipi.gamecritic.controllers.api;

import java.util.List;
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
import it.unipi.gamecritic.repositories.Game.GameRepositoryNeo4J;
import it.unipi.gamecritic.repositories.Game.DTO.GameDTO;
import it.unipi.gamecritic.repositories.User.UserRepository;
import it.unipi.gamecritic.repositories.User.UserRepositoryNeo4J;
import it.unipi.gamecritic.repositories.User.DTO.UserDTO;
import it.unipi.gamecritic.repositories.UserImage.UserImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAPI {
    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserRepositoryNeo4J userRepositoryNeo4J;
    private final GameRepositoryNeo4J gameRepositoryNeo4J;
    private static final Logger logger = LoggerFactory.getLogger(UserAPI.class);

    @Autowired
    public UserAPI(
        UserRepository userRepository, 
        UserImageRepository userImageRepository, 
        UserRepositoryNeo4J userRepositoryNeo4J,
        GameRepositoryNeo4J gameRepositoryNeo4J) {
        this.userRepository = userRepository;
        this.userImageRepository = userImageRepository;
        this.userRepositoryNeo4J = userRepositoryNeo4J;
        this.gameRepositoryNeo4J = gameRepositoryNeo4J;
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
            if (follow)
            {
                userRepositoryNeo4J.addFollowRelationship(user.username, username);
            }
            else
            {
                userRepositoryNeo4J.removeFollowRelationship(user.username, username);
            }
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
            Boolean follows = userRepositoryNeo4J.getFollowRelationship(user.username, username);
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
                user.email = email;
            }
            if (password != null)
            {
                try {
                    user.setPasswordHash(password);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing password");
                }
            }
            userRepository.updateUser(user, password != null, email != null);
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
        public Vector<String> users;
        public Vector<String> games;
        public SuggestionResponse(Vector<String> users, Vector<String> games)
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
            List<UserDTO> userSuggestions = userRepositoryNeo4J.findSuggestedUsers(user.username);
            List<GameDTO> gameSuggestions = gameRepositoryNeo4J.findSuggestedGames(user.username);
            Vector<String> users = new Vector<String>();
            Vector<String> games = new Vector<String>();
            for (UserDTO suggestion : userSuggestions)
            {
                users.add(suggestion.username);
            }
            for (GameDTO suggestion : gameSuggestions)
            {
                games.add(suggestion.name);
            }
            SuggestionResponse response = new SuggestionResponse(users, games);
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
