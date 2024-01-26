package it.unipi.gamecritic.repositories.Comment;

import java.util.List;

import it.unipi.gamecritic.entities.Comment;

public interface CustomCommentRepository {
    List<Comment> findByReviewId(String reviewId);
    String insertComment(Comment comment);
}
