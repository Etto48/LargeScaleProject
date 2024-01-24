package it.unipi.gamecritic.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.mongodb.DBObject;
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
import it.unipi.gamecritic.repositories.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class GameController {
	private final GameRepository gameRepository;
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	@Autowired
	public GameController(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}
    @RequestMapping(value = "/game/{name}")
    public String game(@PathVariable(value="name") String name, Model model, HttpServletRequest request, HttpSession session) {


		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		List<String> excludedNames = Arrays.asList("Name","Description", "reviews", "user_reviews","img","user_review","_id","Top3ReviewsByLikes","reviewCount","Released","Publishers","Developers","Critics");
		model.addAttribute("excludedNames", excludedNames);

		// get the game from the db
		List<DBObject> dbo = gameRepository.findByDynamicAttribute("Name",name);
		List<Game> games = new ArrayList<>();
		for (DBObject o : dbo) {
			Game ga = new Game(o);
			games.add(ga);
		}
        //List<Game> games = GameRepository.getMockupList();
	
		boolean found = false;
		for (Game game : games) {
			if (game.name.equals(name)) {
				model.addAttribute("game", game);
				found = true;
			}
		}
		if (found) {
			return "game";
		}
		else
		{
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
		}
    }

	@RequestMapping("/game/{name}/reviews") 
	public String game_reviews(@PathVariable(value="name") String name, Model model, HttpServletRequest request, HttpSession session) {
		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		
		// TODO: get the reviews from the db
		Vector<Review> reviews = new Vector<Review>();
		reviews.add(new Review() {
			{
				id=0;
				author = "Pippo";
				date = "2015-05-12";
				game = "The Witcher 3: Wild Hunt";
				quote = "The Witcher 3: Wild Hunt is a thoughtful, diverse, and frequently awe-inspiring adventure. Its stories are deep and satisfying, unafraid to touch on themes of personal character, presenting players with choices and consequences that aren’t about turning into a hero or a villain. In the end, it’s quite simply one of the best RPGs ever made.";
				score = 9;
			}
		});
		reviews.add(new Review() {
			{
				id=1;
				author = "Pluto";
				date = "2015-05-12";
				game = "The Witcher 3: Wild Hunt";
				quote = "Wild Hunt is an immaculately detailed adventure, with evocative writing, jaw-droppingly gorgeous art, and a neck-snapping soundtrack. But it’s the stories that you weave, with the consequences of your choices echoing across the game world, that make Wild Hunt something special. In a time of constant sword and sorcery overkill, Wild Hunt is an exemplary reminder of what can be achieved, even by a small team, even with an old-fashioned approach.";
				score = 6;
			}
		});
		reviews.add(new Review() {
			{
				id=2;
				author = "Paperino";
				date = "2015-05-12";
				game = "The Witcher 3: Wild Hunt";
				quote = "The Witcher 3: Wild Hunt encompasses what I hope is the future of RPGs. It stands out for its wonderful writing, variety of quests and things to do in the world, and how your choices have impact on the political whirlwind around you. Usually something is sacrificed when creating a world this ambitious, but everything felt right on cue. I couldn’t put it down, and it’s the first RPG in a long time that I’ve wanted to play through twice.";
				score = 3;
			}
		});
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
			score_distribution.set(review.score - 1, score_distribution.get(review.score - 1) + 1);
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
