package it.unipi.gamecritic.controllers;

import java.util.Vector;

import it.unipi.gamecritic.entities.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Index {
    @RequestMapping(value = "/")
	public String home(Model model) {
		Vector<Game> games = new Vector<Game>();
		games.add(new Game() {
			{
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
				genre = "Action RPG";
				perspective = "Third-person";
				gameplay = "Open world";
				setting = "Fantasy";
				description = "The Witcher 3: Wild Hunt is a 2015 action role-playing game developed and published by CD Projekt and based on The Witcher series of fantasy novels by Andrzej Sapkowski. It is the sequel to the 2011 game The Witcher 2: Assassins of Kings and the third main installment in the The Witcher's video game series, played in an open world with a third-person perspective. Players control protagonist Geralt of Rivia, a monster slayer (known as a Witcher) who is looking for his missing adopted daughter on the run from the Wild Hunt, an otherworldly force determined to capture her and use her powers. Players battle the game's many dangers with weapons and magic, interact with non-player characters, and complete main-story and side quests to acquire experience points and gold, which are used to increase Geralt's abilities and purchase equipment. Its central story has several endings, determined by the player's choices at certain points in the game.";
				user_score = 9;
				img = "https://upload.wikimedia.org/wikipedia/en/0/0c/Witcher_3_cover_art.jpg";
			}
		});
		games.add(new Game() {
			{
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
				genre = "Action RPG";
				perspective = "First-person";
				gameplay = "Open world";
				setting = "Fantasy";
				description = "The Elder Scrolls V: Skyrim is an action role-playing video game developed by Bethesda Game Studios and published by Bethesda Softworks. It is the fifth main installment in The Elder Scrolls series, following The Elder Scrolls IV: Oblivion, and was released worldwide for Microsoft Windows, PlayStation 3, and Xbox 360 on November 11, 2011. The game's main story revolves around the player's character, the Dragonborn, on their quest to defeat Alduin the World-Eater, a dragon who is prophesied to destroy the world. The game is set 200 years after the events of Oblivion and takes place in Skyrim, the northernmost province of Tamriel. Over the course of the game, the player completes quests and develops the character by improving skills. The game continues the open-world tradition of its predecessors by allowing the player to travel anywhere in the game world at any time, and to ignore or postpone the main storyline indefinitely.";
				user_score = 8;
				img = "https://upload.wikimedia.org/wikipedia/en/1/15/The_Elder_Scrolls_V_Skyrim_cover.png";
			}
		});
		model.addAttribute("games", games);
		return "index";
	}
}
