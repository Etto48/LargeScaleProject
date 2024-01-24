// User

package it.unipi.gamecritic.entities.user;

import java.util.Vector;

import it.unipi.gamecritic.controllers.api.GameAPI;
import it.unipi.gamecritic.entities.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("users")
public class User {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(GameAPI.class);
    @Id
    public String id;
    @Field("username")
    public String username;
    @Field("password_hash")
    public String password_hash;
    @Field("email")
    public String email;
    @Field("Top3ReviewsByLikes")
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
    public String getUsername() {
        return this.username;
    }
}
