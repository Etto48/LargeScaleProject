package it.unipi.gamecritic.entities;

import java.util.Vector;

public class Company {
    public String name;
    public String overview;
    public String img;
    public Vector<Game> top_games;

    public Company(String name, String overview, String img, Vector<Game> top_games) {
        this.name = name;
        this.overview = overview;
        this.img = img;
        this.top_games = top_games;
    }
}
