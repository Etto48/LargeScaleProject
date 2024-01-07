package it.unipi.gamecritic.entities;

public class Review {
    public Integer id;
    public Integer game_id;
    public String game;
    public String author;
    public String date;
    public String quote;
    public Integer score;

    public Review(Integer id, Integer game_id, String game, String author, String date, String quote, Integer score) {
        this.id = id;
        this.game_id = game_id;
        this.game = game;
        this.author = author;
        this.date = date;
        this.quote = quote;
        this.score = score;
    }

    public Review() {
    }
}
