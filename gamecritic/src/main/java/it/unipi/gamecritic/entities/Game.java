package it.unipi.gamecritic.entities;

import java.util.Vector;

public class Game {
    public Integer id;
    public String name;
    public String release;
    public Vector<String> publishers;
    public Vector<String> developers;
    public Vector<String> genres;
    public Vector<String> platforms;
    public String perspective;
    public String gameplay;
    public String setting;
    public String description;
    public Float user_score;
    public String img;
    public Vector<Review> top_reviews;

    public Game(
        Integer id,
        String name,
        Float user_score,
        String description
        ) {
        this.id = id;
        this.name = name;
        this.user_score = user_score;
        this.description = description;
    }

    public Game(
        Integer id, 
        String name, 
        String release, 
        Vector<String> publishers, 
        Vector<String> developers, 
        Vector<String> genres, 
        Vector<String> platforms,
        String perspective, 
        String gameplay, 
        String setting, 
        String description, 
        Float user_score, 
        String img, 
        Vector<Review> top_reviews
        ) {
        this.id = id;
        this.name = name;
        this.release = release;
        this.publishers = publishers;
        this.developers = developers;
        this.platforms = platforms;
        this.genres = genres;
        this.perspective = perspective;
        this.gameplay = gameplay;
        this.setting = setting;
        this.description = description;
        this.user_score = user_score;
        this.img = img;
        this.top_reviews = top_reviews;
    }

    public Game() {
    }
}
