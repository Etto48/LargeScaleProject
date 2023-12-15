package it.unipi.gamecritic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Index {
    @RequestMapping(value = "/")
	public String home(Model model) {
		model.addAttribute("hello_text", "Ciao!");
		return "index";
	}
}
