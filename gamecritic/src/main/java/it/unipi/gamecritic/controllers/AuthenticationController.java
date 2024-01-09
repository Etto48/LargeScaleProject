package it.unipi.gamecritic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthenticationController {
    @RequestMapping(value = "/authentication", method = RequestMethod.GET)
    public String login(
        @RequestParam(value = "mode", required = false) String mode,
        Model model, 
        HttpServletRequest request, 
        HttpSession session) 
    {
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
}
