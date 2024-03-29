package it.unipi.gamecritic.entities;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class Company {
    @Id
    public ObjectId id;
    @Field("Name")
    public String name;
    @Field("Overview")
    public String overview;
    @Field("imglink")
    public String img;
    @Field("Top3Games")
    public List<Document> top_games;

    public Company(String name, String overview, String img, List<Document> top_games) {
        this.name = name;
        this.overview = overview;
        this.img = img;
        this.top_games = top_games;
    }
}
