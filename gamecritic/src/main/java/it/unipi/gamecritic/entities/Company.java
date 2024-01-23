package it.unipi.gamecritic.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Vector;

public class Company {
    @Id
    public String _id;
    @Field("Name")
    public String name;
    @Field("Overview")
    public String overview;
    @Field("imglink")
    public String img;
    @Field("Top3Games")
    public Vector<Game> top_games;

    public Company(String name, String overview, String img, Vector<Game> top_games) {
        this.name = name;
        this.overview = overview;
        this.img = img;
        this.top_games = top_games;
    }
}
