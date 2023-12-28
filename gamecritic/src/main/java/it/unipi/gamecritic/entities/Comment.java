package it.unipi.gamecritic.entities;

import java.util.Vector;

public class Comment {
    public String author;
    public Integer review_id;
    public String quote;
    public Vector<Comment> comments;
}
