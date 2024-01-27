package it.unipi.gamecritic.controllers.api;

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
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.Gson;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.Comment.CommentRepository;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import it.unipi.gamecritic.repositories.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminAPI {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public AdminAPI(UserRepository userRepository, ReviewRepository reviewRepository, GameRepository gameRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
        this.commentRepository = commentRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(AdminAPI.class);
    public void ban(String username, User user) {
        userRepository.deleteUser(username);
        logger.info("Ban user \"" + username + "\" by " + user.username);
    }

    public void delete_review(String id, User user) {
        reviewRepository.deleteReview(id);
        logger.info("Delete review " + id + " by " + user.username);
    }

    public void delete_comment(String id, User user) {
        commentRepository.deleteComment(id);
        logger.info("Delete comment " + id + " by " + user.username);
    }

    public void delete_game(String name, User user) {
        gameRepository.deleteGame(name);
        logger.info("Delete game \"" + name + "\" by " + user.username);
    }


    @RequestMapping(value = "/api/admin/ban", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String admin_ban(
        @RequestParam(value = "username", required = true) String username,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                ban(username, user); 
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/admin/delete/review", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String admin_delete_review(
        @RequestParam(value = "id", required = true) String id,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                delete_review(id, user);
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/admin/delete/comment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String admin_delete_comment(
        @RequestParam(value = "id", required = true) String id,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                delete_comment(id, user);
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    @RequestMapping(value = "/api/admin/delete/game", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String admin_delete_game(
        @RequestParam(value = "name", required = true) String name,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                delete_game(name, user);
                return "{}";
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }

    public class TerminalResponse {
        public String text;
        public Boolean error;

        public TerminalResponse(String text, Boolean error) {
            this.text = text;
            this.error = error;
        }
    }

    public Vector<String> tokenize(String command) {
        Vector<String> tokens = new Vector<String>();
        String token = "";
        boolean in_quotes = false;
        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);
            if (c == '"') {
                in_quotes = !in_quotes;
            } else if (c == ' ' && !in_quotes) {
                if (token.length() > 0) {
                    tokens.add(token);
                    token = "";
                }
            } else {
                token += c;
            }
        }
        if (token.length() > 0) {
            tokens.add(token);
        }
        return tokens;
    }

    @RequestMapping(value = "/api/admin/terminal", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String admin_terminal(
        @RequestParam(value = "command", required = true) String command,
        HttpServletRequest request,
        HttpSession session)
    {
        User user = (User) session.getAttribute("user");
        if (user != null)
        {
            if(user.getAccountType().equals("Admin"))
            {
                Vector<String> tokens = tokenize(command);
                Gson gson = new Gson();
                if (tokens.size() == 0) {
                    return gson.toJson(new TerminalResponse("", false));
                }
                switch (tokens.get(0)) {
                    case "ban":
                        if (tokens.size() == 2) {
                            ban(tokens.get(1), user);
                            return gson.toJson(new TerminalResponse("User \"" + tokens.get(1) + "\" banned", false));
                        }else
                        {
                            return gson.toJson(new TerminalResponse("Usage: ban <username>", true));
                        }
                    case "delete":
                        if (tokens.size() == 3) {
                            switch (tokens.get(1)) {
                                case "review":
                                    delete_review(tokens.get(2), user);
                                    break;
                                case "comment":
                                    delete_comment(tokens.get(2), user);
                                    break;
                                case "game":
                                    delete_game(tokens.get(2), user);
                                    break;
                                default:
                                    return gson.toJson(new TerminalResponse("Usage: delete <review|comment|game> <id|name>", true));
                            }
                            return gson.toJson(new TerminalResponse(tokens.get(1) + " \"" + tokens.get(2) + "\" deleted", false));
                        } else {
                            return gson.toJson(new TerminalResponse("Usage: delete <review|comment|game> <id|name>", true));
                        }
                    case "help":
                        return gson.toJson(new TerminalResponse("Available commands:\nban <username>\ndelete <review|comment|game> <id|name>\nhelp\n", false));
                    default:
                        return gson.toJson(new TerminalResponse("Command not found, use \"help\" for a list of available commands", true));
                }
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in to perform this action");
        }
    }
}
