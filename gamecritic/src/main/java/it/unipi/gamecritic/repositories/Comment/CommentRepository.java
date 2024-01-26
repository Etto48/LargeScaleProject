package it.unipi.gamecritic.repositories.Comment;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.unipi.gamecritic.entities.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>, CustomCommentRepository {
    
}
