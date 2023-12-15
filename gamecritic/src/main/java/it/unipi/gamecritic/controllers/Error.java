package it.unipi.gamecritic.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class Error implements ErrorController {
    @RequestMapping(value = "/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null)
        {
            model.addAttribute("status", status.toString());
        }
        else
        {
            model.addAttribute("status", "Unknown");
        }
        return "error";
    }
}
