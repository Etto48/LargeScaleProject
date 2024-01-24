package it.unipi.gamecritic.controllers.api;

import it.unipi.gamecritic.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.unipi.gamecritic.entities.user.Admin;
import it.unipi.gamecritic.entities.user.CompanyManager;
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
        
        User user = null;
        if(username.equals("company"))
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
        }
        user.username = username;
        user.password_hash = password;
        user.email = "";
        user.top_reviews = null;

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
