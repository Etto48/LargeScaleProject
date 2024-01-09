package it.unipi.gamecritic.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Company {
    @RequestMapping("/company_panel")
    public String company(
        Model model,
        HttpServletRequest request,
        HttpSession session) {
        if (session.getAttribute("user") == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not logged in");
        } else {
            it.unipi.gamecritic.entities.user.User user = (it.unipi.gamecritic.entities.user.User) session.getAttribute("user");
            if (user.getAccountType().equals("Company")) {
                model.addAttribute("company", (it.unipi.gamecritic.entities.user.Company)user);
                model.addAttribute("user", user);
                model.addAttribute("request", request);
                return "company_panel";
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a company");
            }
        }
    }
}