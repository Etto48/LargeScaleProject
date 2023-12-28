package it.unipi.gamecritic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Authentication {
    @RequestMapping("/authentication")
    public String login(Model model, HttpServletRequest request, HttpSession session,
        @RequestParam(value = "mode", required = false) String mode) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        if (mode != null && mode.equals("login")) {
            model.addAttribute("default_mode", "login");
        }
        else {
            model.addAttribute("default_mode", "sign_up");
        }
        
        model.addAttribute("request", request);
        return "authentication";
    }

    @RequestMapping(value = "/process_login", method = RequestMethod.POST)
    @ResponseBody
    public String processLogin(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam("username") String username, 
        @RequestParam("password") String password) {
        
        // TODO: check if username and password are valid

        it.unipi.gamecritic.entities.User user = new it.unipi.gamecritic.entities.User();
        user.username = username;
        user.password_hash = password;
        user.email = "";
        user.top_reviews = null;

        session.setAttribute("user", user);
        
        
        return "success";
    }

    @RequestMapping(value = "/process_sign_up", method = RequestMethod.POST)
    @ResponseBody
    public String processSignUp(
        Model model, 
        HttpServletRequest request, 
        HttpSession session,
        @RequestParam("username") String username, 
        @RequestParam("password") String password,
        @RequestParam("email") String email) 
    {
        
        // TODO: check if username and password are valid
        // TODO: insert user in database

        it.unipi.gamecritic.entities.User user = new it.unipi.gamecritic.entities.User();
        user.username = username;
        user.password_hash = password;
        user.email = email;
        user.top_reviews = null;
        
        session.setAttribute("user", user);

        return "success";
    }


    @RequestMapping("/logout")
    public String logout(Model model, HttpServletRequest request, HttpSession session) throws ServletException {
        session.invalidate();
        model.addAttribute("request", request);
        return "redirect:/";
    }
}
