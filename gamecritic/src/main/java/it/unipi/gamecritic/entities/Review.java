package it.unipi.gamecritic.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Review {
    @Id
    public ObjectId id;
    public String game;
    public Integer score;
    public String quote;
    public String author;
    public String date;

    public Review() {
    }

    public Long getId() {
        return Long.parseLong(id.toHexString(),16);
    }
}
