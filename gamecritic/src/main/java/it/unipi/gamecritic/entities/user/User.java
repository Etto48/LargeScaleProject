// User

package it.unipi.gamecritic.entities.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Vector;

import it.unipi.gamecritic.entities.Review;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.DBObject;

@Document("users")
public class User {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    @Id
    public ObjectId id;
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

    public User(DBObject dbObject)
    {
        this.id = (ObjectId) dbObject.get("_id");
        this.username = (String) dbObject.get("username");
        this.password_hash = (String) dbObject.get("password_hash");
        this.email = (String) dbObject.get("email");
        @SuppressWarnings("unchecked")
        ArrayList<Review> top_review = (ArrayList<Review>) dbObject.get("Top3ReviewsByLikes");
        this.top_reviews = top_review.stream().collect(Vector::new, Vector::add, Vector::addAll);
    }

    public static User userFactory(DBObject dbObject)
    {
        if(dbObject.get("company_name") != null)
            return new CompanyManager(dbObject);
        else if(dbObject.get("is_admin") != null && (boolean)dbObject.get("is_admin"))
            return new Admin(dbObject);
        else
            return new User(dbObject);
    }

    public String getAccountType() {
        return "User";
    }
    public String getUsername() {
        return this.username;
    }

    private String encodePassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(digest);
    }

    public void setPasswordHash(String password) throws NoSuchAlgorithmException{
        this.password_hash = encodePassword(password);
    }

    public boolean checkPassword(String password) throws NoSuchAlgorithmException{
        return this.password_hash.equals(encodePassword(password));
    }
}
