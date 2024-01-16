package it.unipi.gamecritic.repositories;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.data.mongodb.repository.MongoRepository;

//
public interface GameRepository extends MongoRepository<Game, String>, CustomGameRepository{

    public static List<Game> getMockupList()
    {
        Vector<Game> games = new Vector<>();
        Vector<String> publishers = new Vector<>();
        publishers.add("Nintendo");
        Vector<String> developers = new Vector<>();
        developers.add("Nintendo");
        Vector<String> genres = new Vector<>();
        genres.add("Action");
        genres.add("Adventure");
        Vector<String> platforms = new Vector<>();
        platforms.add("Nintendo Switch");
        platforms.add("Wii U");
        games.add(new Game(
            "The Legend of Zelda: Breath of the Wild", 
            "2021-03-03",
            Map.of(
                "publishers", publishers,
                "developers", developers,
                "genres", genres,
                "platforms", platforms,
                "user_score", 9f,
                "description", "The Legend of Zelda: Breath of the Wild is an action-adventure game developed and published by Nintendo, released for the Nintendo Switch and Wii U consoles on March 3, 2017. Breath of the Wild is set at the end of the Zelda timeline; the player controls Link, who awakens from a hundred-year slumber to defeat Calamity Ganon before it can destroy the kingdom of Hyrule.",
                "img", "https://upload.wikimedia.org/wikipedia/en/c/c6/The_Legend_of_Zelda_Breath_of_the_Wild.jpg"
            ),
            List.of(
                new Review(0, 0, "The Legend of Zelda: Breath of the Wild", "Pippo", "2021-03-03", "This game is awesome!", 9),
                new Review(1, 0, "The Legend of Zelda: Breath of the Wild", "Pluto", "2021-03-03", "This game is awesome!", 6),
                new Review(2, 0, "The Legend of Zelda: Breath of the Wild", "Paperino", "2021-03-03", "This game is awesome!", 3)
            )
        ));
        games.add(new Game(
            "Super Mario Odyssey", 
            "2021-03-03",
            Map.of(
                "publishers", publishers,
                "developers", developers,
                "genres", genres,
                "platforms", platforms,
                "user_score", 5.2f,
                "description", "Super Mario Odyssey is a 2017 platform game developed and published by Nintendo for the Nintendo Switch. An entry in the Super Mario series, it follows Mario and Cappy, a sentient hat that allows Mario to control other characters and objects, as they journey across various worlds to save Princess Peach from his nemesis Bowser, who plans to forcibly marry her.",
                "img", "https://upload.wikimedia.org/wikipedia/it/thumb/8/8d/Super_Mario_Odyssey.jpg/555px-Super_Mario_Odyssey.jpg?20171117094306"
            ),
            List.of(
                new Review(0, 1, "Super Mario Odyssey", "Pippo", "2021-03-03", "This game is awesome!", 9),
                new Review(1, 1, "Super Mario Odyssey", "Pluto", "2021-03-03", "This game is awesome!", 6),
                new Review(2, 1, "Super Mario Odyssey", "Paperino", "2021-03-03", "This game is awesome!", 3)
            )
        ));
        games.add(new Game(
            "Super Smash Bros. Ultimate", 
            "2021-03-03",
            Map.of(
                "publishers", publishers,
                "developers", developers,
                "genres", genres,
                "platforms", platforms,
                "user_score", 1.9f,
                "description", "Super Smash Bros. Ultimate is a 2018 crossover fighting game developed by Bandai Namco Studios and Sora Ltd. and published by Nintendo for the Nintendo Switch. It is the fifth installment in the Super Smash Bros. series, succeeding Super Smash Bros. for Nintendo 3DS and Wii U. The game follows the series' traditional style of gameplay: controlling one of the various characters, players must use differing attacks to weaken their opponents and knock them out of an arena.",
                "img", "https://upload.wikimedia.org/wikipedia/en/5/50/Super_Smash_Bros._Ultimate.jpg"
            ),
            List.of(
                new Review(0, 2, "Super Smash Bros. Ultimate", "Pippo", "2021-03-03", "This game is awesome!", 9),
                new Review(1, 2, "Super Smash Bros. Ultimate", "Pluto", "2021-03-03", "This game is awesome!", 6),
                new Review(2, 2, "Super Smash Bros. Ultimate", "Paperino", "2021-03-03", "This game is awesome!", 3)
            )
        ));
        return games;
    }
}
