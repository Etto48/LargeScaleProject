package it.unipi.gamecritic.entities;

import java.util.Vector;

public class User {
    public String username;
    public String password_hash;
    public String email;
    public String image_data;
    public Vector<Review> top_reviews;
}
