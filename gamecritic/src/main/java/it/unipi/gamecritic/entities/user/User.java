package it.unipi.gamecritic.entities.user;

import java.util.Vector;

import it.unipi.gamecritic.entities.Review;

public class User {
    public String username;
    public String password_hash;
    public String email;
    public Vector<Review> top_reviews;

    public User(String username, String password_hash, String email, Vector<Review> top_reviews) {
        this.username = username;
        this.password_hash = password_hash;
        this.email = email;
        this.top_reviews = top_reviews;
    }

    public User() {
    }

    public String getAccountType() {
        return "User";
    }
}