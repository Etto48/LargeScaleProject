package it.unipi.gamecritic.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.user.Admin;
import it.unipi.gamecritic.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
    @RequestMapping("/control_panel")
    public String admin(
        Model model,
        HttpServletRequest request,
        HttpSession session) 
    {
        if (session.getAttribute("user") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in");
        } else {
            User user = (User) session.getAttribute("user");
            if (user.getAccountType().equals("Admin")) {
                model.addAttribute("admin", (Admin)user);
                model.addAttribute("user", user);
                model.addAttribute("request", request);
                return "control_panel";
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not an admin");
            }
        }
    }
}
