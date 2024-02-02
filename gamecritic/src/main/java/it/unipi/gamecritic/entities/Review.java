package it.unipi.gamecritic.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.mongodb.DBObject;

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

    /**
     *  Set date to null if you want to use the current date
     **/
    public Review(String game, Integer score, String quote, String author, String date)
    {
        this.game = game;
        this.score = score;
        this.quote = quote;
        this.author = author;
        if (date == null)
        {
            Date d = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            this.date = df.format(d);
        }
        else
        {
            this.date = date;
        }
    }

    public Review(DBObject dbObject)
    {
        this.id = (ObjectId) dbObject.get("_id");
        this.game = (String) dbObject.get("game");
        this.score = (Integer) dbObject.get("score");
        this.quote = (String) dbObject.get("quote");
        this.author = (String) dbObject.get("author");
        this.date = (String) dbObject.get("date");
    }

    public Review(Document document)
    {
        this.id = (ObjectId) document.get("_id");
        this.game = (String) document.get("game");
        this.score = (Integer) document.get("score");
        this.quote = (String) document.get("quote");
        this.author = (String) document.get("author");
        this.date = (String) document.get("date");
    }

    public String getId() {
        return id.toHexString();
    }
}
