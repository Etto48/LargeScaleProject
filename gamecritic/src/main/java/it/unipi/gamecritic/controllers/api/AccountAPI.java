package it.unipi.gamecritic.controllers.api;

import it.unipi.gamecritic.repositories.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

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
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountAPI {
    @SuppressWarnings("unused")
    private final UserRepository userRepository;
    
    @Autowired
    public AccountAPI(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    public String processLogin(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam(value = "username", required = true) String username, 
        @RequestParam(value = "password", required = true) String password) {
        
        // TODO: check if username and password are valid
        List<User> users = userRepository.findByDynamicAttribute("username", username);
        if (users.size() == 0)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        User user = users.get(0);
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            Base64.Encoder encoder = Base64.getEncoder();
            String input_password_hash = encoder.encodeToString(hash);
            if (!user.password_hash.equals(input_password_hash))
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }

        /*if(username.equals("company"))
        {
            user = new CompanyManager();
        }
        else if (username.equals("admin"))
        {
            user = new Admin();
        }
        else
        {
            user = new User();
        }*/

        session.setAttribute("user", user);        
        
        return "success";
    }

    @RequestMapping(value = "/api/sign_up", method = RequestMethod.POST)
    @ResponseBody
    public String processSignUp(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam(value = "username", required = true) String username, 
        @RequestParam(value = "password", required = true) String password,
        @RequestParam(value = "email", required = true) String email) 
    {
        
        // TODO: check if username and password are valid
        // TODO: insert user in database


        User user = new User();
        user.username = username;
        user.password_hash = password;
        user.email = email;
        user.top_reviews = null;

        session.setAttribute("user", user);

        return "success";
    }


    @RequestMapping("/api/logout")
    @ResponseBody
    public String logout(Model model, HttpServletRequest request, HttpSession session) throws ServletException {
        session.invalidate();
        return "success";
    }
}
