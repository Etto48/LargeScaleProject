package it.unipi.gamecritic.entities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Comment {
    @Id
    public ObjectId id;
    public ObjectId reviewId;
    public String author;
    public String quote;
    public String date;

    public Comment() {}

    /**
     * Set date to null if you want to use the current date
     */
    public Comment(String reivew_id, String author, String quote, String date) {
        this.reviewId = new ObjectId(reivew_id);
        this.author = author;
        this.quote = quote;
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
    
    public String getId() {
        return id.toHexString();
    }

    public String getReviewId() {
        return reviewId.toHexString();
    }
}
