package it.unipi.gamecritic.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.Review.ReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameController {
	private final ReviewRepository reviewRepository;
	private final GameRepository gameRepository;
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	@Autowired
	public GameController(ReviewRepository reviewRepository, GameRepository gameRepository) {
		this.reviewRepository = reviewRepository;
		this.gameRepository = gameRepository;
	}
    @RequestMapping(value = "/game/{name}")
    public String game(@PathVariable(value="name") String name, Model model, HttpServletRequest request, HttpSession session) {


		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		List<String> excludedNames = Arrays.asList("Name","Description", "reviews", "user_reviews","img","user_review","_id","Top3ReviewsByLikes","reviewCount","Released","Publishers","Developers","Critics");
		model.addAttribute("excludedNames", excludedNames);

		List<Game> games = gameRepository.findByDynamicAttribute("Name",name);
		
		if (!games.isEmpty()) {
			model.addAttribute("game", games.get(0));
			return "game";
		}
		else
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		}
    }

	private static int clamp_score(int value)
	{
		if (value < 1)
		{
			return 1;
		}
		else if (value > 10)
		{
			return 10;
		}
		else
		{
			return value;
		}
	}

	@RequestMapping("/game/{name}/reviews") 
	public String game_reviews(
		@PathVariable(value="name") String name, 
		Model model, 
		HttpServletRequest request, 
		HttpSession session) 
	{
		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		
		List<Review> reviews = reviewRepository.findByGame(name);

		if (reviews.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		}
		Float avg_score = 0f;
		for (Review review : reviews) {
			avg_score += review.score;
		}
		avg_score /= reviews.size();
		Vector<Float> score_distribution = new Vector<Float>();
		for (int i = 0; i < 10; i++) {
			score_distribution.add(0f);
		}
		for (Review review : reviews) {
			score_distribution.set(clamp_score(review.score) - 1, score_distribution.get(clamp_score(review.score) - 1) + 1);
		}
		for (int i = 0; i < 10; i++) {
			score_distribution.set(i, score_distribution.get(i) / reviews.size() * 100);
		}
		model.addAttribute("score_distribution", score_distribution);
		model.addAttribute("avg_score", avg_score);
		model.addAttribute("reviews", reviews);
		model.addAttribute("game_name", name);
		
		return "game_reviews";
	}
}
