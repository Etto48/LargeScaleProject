package it.unipi.gamecritic.controllers.api;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.user.User;
import it.unipi.gamecritic.repositories.User.UserRepository;
import it.unipi.gamecritic.entities.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountAPI {
    private final UserRepository userRepository;
    
    @Autowired
    public AccountAPI(
        UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/api/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String processLogin(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam(value = "username", required = true) String username, 
        @RequestParam(value = "password", required = true) String password) {
        
        List<User> users = userRepository.findByDynamicAttribute("username", username);
        if (users.size() == 0)
        {
            return "\"Invalid username or password\"";
        }

        User user = users.get(0);
        try
        {
            if (!user.checkPassword(password))
            {
                return "\"Invalid username or password\"";
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

        session.setAttribute("user", user);        
        
        return "\"success\"";
    }

    @RequestMapping(value = "/api/sign_up", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String processSignUp(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam(value = "username", required = true) String username, 
        @RequestParam(value = "password", required = true) String password,
        @RequestParam(value = "email", required = true) String email) 
    {
        User user = new User();
        user.username = username;
        try
        {
            user.setPasswordHash(password);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
        user.email = email;
        user.top_reviews = new Vector<Review>();

        if(userRepository.insertUserIfNotExists(user))
        {
            session.setAttribute("user", user);
            return "\"success\"";
        }
        else
        {
            return "\"Username already exists\"";
        }
    }


    @RequestMapping(value = "/api/logout", produces = "application/json")
    @ResponseBody
    public String logout(Model model, HttpServletRequest request, HttpSession session) throws ServletException {
        session.invalidate();
        return "\"success\"";
    }
}
