package it.unipi.gamecritic.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.repositories.GameRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	private final GameRepository gameRepository;
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	public IndexController(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}
    @RequestMapping(value = "/")
	public String home(Model model, HttpServletRequest request, HttpSession session) {
		model.addAttribute("request", request);
		model.addAttribute("user", session.getAttribute("user"));
		List<DBObject> game = gameRepository.findByDynamicAttribute("Name","Halo 2");

		if (game == null){

		}
		for (String key: game.get(0).keySet()){
			logger.info("oren");
			Object o = game.get(0).get(key);
			while (o instanceof ArrayList<?>){
				logger.info(o.getClass().toString());
				o = ((ArrayList<?>) o).get(0);
			}
			logger.info(game.get(0).get(key).getClass().getName() + " "+ game.get(0).get(key).toString());
		}

		// TODO: get games from database
		Vector<Game> games = new Vector<Game>();
		/*
		games.add(new Game() {
			{
				id=0;
				name = "The Witcher 3: Wild Hunt";
				release = "2015-05-19";
				publishers = new Vector<String>() {
					{
						add("CD Projekt");
					}
				};
				developers = new Vector<String>() {
					{
						add("CD Projekt Red");
					}
				};
				genres = new Vector<String>() {
					{
						add("Action");
						add("RPG");
					}
				};
				perspective = "Third-person";
				gameplay = "Open world";
				setting = "Fantasy";
				description = "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by CD Projekt and based on The Witcher series of fantasy novels by Andrzej Sapkowski. It is the sequel to the 2011 game The Witcher 2: Assassins of Kings and the third main installment in the The Witcher's video game series, played in an open world with a third-person perspective. Players control protagonist Geralt of Rivia, a monster slayer (known as a Witcher) who is looking for his missing adopted daughter on the run from the Wild Hunt, an otherworldly force determined to capture her and use her powers. Players battle the game's many dangers with weapons and magic, interact with non-player characters, and complete main-story and side quests to acquire experience points and gold, which are used to increase Geralt's abilities and purchase equipment. Its central story has several endings, determined by the player's choices at certain points in the game.";
				user_score = 5.6f;
				img = "https://upload.wikimedia.org/wikipedia/en/0/0c/Witcher_3_cover_art.jpg";
				top_reviews = new Vector<Review>() {
					{
						add(new Review() {
							{
								id=0;
								author = "Pippo";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt is a thoughtful, diverse, and frequently awe-inspiring adventure. Its stories are deep and satisfying, unafraid to touch on themes of personal character, presenting players with choices and consequences that aren’t about turning into a hero or a villain. In the end, it’s quite simply one of the best RPGs ever made.";
								score = 9;
							}
						});
						add(new Review() {
							{
								id=1;
								author = "Pluto";
								date = "2015-05-12";
								quote = "Wild Hunt is an immaculately detailed adventure, with evocative writing, jaw-droppingly gorgeous art, and a neck-snapping soundtrack. But it’s the stories that you weave, with the consequences of your choices echoing across the game world, that make Wild Hunt something special. In a time of constant sword and sorcery overkill, Wild Hunt is an exemplary reminder of what can be achieved, even by a small team, even with an old-fashioned approach.";
								score = 6;
							}
						});
						add(new Review() {
							{
								id=2;
								author = "Paperino";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt encompasses what I hope is the future of RPGs. It stands out for its wonderful writing, variety of quests and things to do in the world, and how your choices have impact on the political whirlwind around you. Usually something is sacrificed when creating a world this ambitious, but everything felt right on cue. I couldn’t put it down, and it’s the first RPG in a long time that I’ve wanted to play through twice.";
								score = 3;
							}
						});
					}
				};
			}
		});
		games.add(new Game() {
			{
				id=1;
				name = "The Elder Scrolls V: Skyrim";
				release = "2011-11-11";
				publishers = new Vector<String>() {
					{
						add("Bethesda Softworks");
					}
				};
				developers = new Vector<String>() {
					{
						add("Bethesda Game Studios");
					}
				};
				genres = new Vector<String>() {
					{
						add("Action");
						add("RPG");
					}
				};
				perspective = "First-person";
				gameplay = "Open world";
				setting = "Fantasy";
				description = "The Elder Scrolls V: Skyrim is an action role-playing video game developed by Bethesda Game Studios and published by Bethesda Softworks. It is the fifth main installment in The Elder Scrolls series, following The Elder Scrolls IV: Oblivion, and was released worldwide for Microsoft Windows, PlayStation 3, and Xbox 360 on November 11, 2011. The game's main story revolves around the player's character, the Dragonborn, on their quest to defeat Alduin the World-Eater, a dragon who is prophesied to destroy the world. The game is set 200 years after the events of Oblivion and takes place in Skyrim, the northernmost province of Tamriel. Over the course of the game, the player completes quests and develops the character by improving skills. The game continues the open-world tradition of its predecessors by allowing the player to travel anywhere in the game world at any time, and to ignore or postpone the main storyline indefinitely.";
				user_score = 1.2f;
				img = "https://upload.wikimedia.org/wikipedia/en/1/15/The_Elder_Scrolls_V_Skyrim_cover.png";
				top_reviews = new Vector<Review>() {
					{
						add(new Review() {
							{
								id=0;
								author = "Pippo";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt is a thoughtful, diverse, and frequently awe-inspiring adventure. Its stories are deep and satisfying, unafraid to touch on themes of personal character, presenting players with choices and consequences that aren’t about turning into a hero or a villain. In the end, it’s quite simply one of the best RPGs ever made.";
								score = 9;
							}
						});
						add(new Review() {
							{
								id=1;
								author = "Pluto";
								date = "2015-05-12";
								quote = "Wild Hunt is an immaculately detailed adventure, with evocative writing, jaw-droppingly gorgeous art, and a neck-snapping soundtrack. But it’s the stories that you weave, with the consequences of your choices echoing across the game world, that make Wild Hunt something special. In a time of constant sword and sorcery overkill, Wild Hunt is an exemplary reminder of what can be achieved, even by a small team, even with an old-fashioned approach.";
								score = 6;
							}
						});
						add(new Review() {
							{
								id=2;
								author = "Paperino";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt encompasses what I hope is the future of RPGs. It stands out for its wonderful writing, variety of quests and things to do in the world, and how your choices have impact on the political whirlwind around you. Usually something is sacrificed when creating a world this ambitious, but everything felt right on cue. I couldn’t put it down, and it’s the first RPG in a long time that I’ve wanted to play through twice.";
								score = 3;
							}
						});
					}
				};
			}
		});

		games.add(new Game() {
			{
				id=2;
				name = "The Legend of Zelda: Breath of the Wild";
				release = "2017-03-03";
				publishers = new Vector<String>() {
					{
						add("Nintendo");
					}
				};
				developers = new Vector<String>() {
					{
						add("Nintendo EPD");
					}
				};
				genres = new Vector<String>() {
					{
						add("Action");
						add("Adventure");
					}
				};
				perspective = "Third-person";
				gameplay = "Open world";
				setting = "Fantasy";
				description = "The Legend of Zelda: Breath of the Wild is a 2017 action-adventure game developed and published by Nintendo for the Nintendo Switch and Wii U consoles. Breath of the Wild is part of the Legend of Zelda franchise and is set at the end of the series' timeline; the player controls Link, who awakens from a hundred-year slumber to defeat Calamity Ganon before it can destroy the kingdom of Hyrule.";
				user_score = 8f;
				img = "https://upload.wikimedia.org/wikipedia/en/c/c6/The_Legend_of_Zelda_Breath_of_the_Wild.jpg";
				top_reviews = new Vector<Review>() {
					{
						add(new Review() {
							{
								id=0;
								author = "Pippo";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt is a thoughtful, diverse, and frequently awe-inspiring adventure. Its stories are deep and satisfying, unafraid to touch on themes of personal character, presenting players with choices and consequences that aren’t about turning into a hero or a villain. In the end, it’s quite simply one of the best RPGs ever made.";
								score = 9;
							}
						});
						add(new Review() {
							{
								id=1;
								author = "Pluto";
								date = "2015-05-12";
								quote = "Wild Hunt is an immaculately detailed adventure, with evocative writing, jaw-droppingly gorgeous art, and a neck-snapping soundtrack. But it’s the stories that you weave, with the consequences of your choices echoing across the game world, that make Wild Hunt something special. In a time of constant sword and sorcery overkill, Wild Hunt is an exemplary reminder of what can be achieved, even by a small team, even with an old-fashioned approach.";
								score = 6;
							}
						});
						add(new Review() {
							{
								id=2;
								author = "Paperino";
								date = "2015-05-12";
								quote = "The Witcher 3: Wild Hunt encompasses what I hope is the future of RPGs. It stands out for its wonderful writing, variety of quests and things to do in the world, and how your choices have impact on the political whirlwind around you. Usually something is sacrificed when creating a world this ambitious, but everything felt right on cue. I couldn’t put it down, and it’s the first RPG in a long time that I’ve wanted to play through twice.";
								score = 3;
							}
						});
					}
				};
			}
		});*/

		model.addAttribute("games", games);
		return "index";
	}
}
