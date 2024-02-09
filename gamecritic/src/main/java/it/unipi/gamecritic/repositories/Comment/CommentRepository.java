package it.unipi.gamecritic.repositories.Comment;

import java.util.List;

import org.springframework.stereotype.Component;

import it.unipi.gamecritic.entities.Comment;

@Component
public class CommentRepository implements CustomCommentRepository {
    private final CommentRepositoryMongoDB commentRepositoryMongoDB;
    
    public CommentRepository(CommentRepositoryMongoDB commentRepositoryMongoDB) {
        this.commentRepositoryMongoDB = commentRepositoryMongoDB;
    }

    public List<Comment> findByReviewId(String reviewId)
    {
        return commentRepositoryMongoDB.findByReviewId(reviewId);
    }
    public String insertComment(Comment comment)
    {
        return commentRepositoryMongoDB.insertComment(comment);
    }
    public void deleteComment(String id)
    {
        commentRepositoryMongoDB.deleteComment(id);
    }
}
