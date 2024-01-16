package it.unipi.gamecritic.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping(value = "/")
	public String home(Model model, HttpServletRequest request, HttpSession session) {
		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		return "index";
	}
}
