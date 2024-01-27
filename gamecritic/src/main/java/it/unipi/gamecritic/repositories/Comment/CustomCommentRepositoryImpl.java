package it.unipi.gamecritic.repositories.Comment;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import it.unipi.gamecritic.entities.Comment;

public class CustomCommentRepositoryImpl implements CustomCommentRepository {
    private final MongoTemplate mongoTemplate;
    
    public CustomCommentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Comment> findByReviewId(String reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("The given reviewId must not be null");
        }
        try
        {
            ObjectId id = new ObjectId(reviewId);
            Query query = new Query(Criteria.where("reviewId").is(id));
            return mongoTemplate.find(query, Comment.class, "comments");
        }
        catch(IllegalArgumentException e)
        {
            return null;
        }
    }

    @Override
    public String insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("The given comment must not be null");
        }
        mongoTemplate.insert(comment, "comments");
        return comment.getId();
    }

    @Override
    public void deleteComment(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The given id must not be null");
        }
        try
        {
            ObjectId oid = new ObjectId(id);
            Query query = new Query(Criteria.where("_id").is(oid));
            mongoTemplate.remove(query, Comment.class, "comments");
        }
        catch(IllegalArgumentException e)
        {
            return;
        }
    }
}
