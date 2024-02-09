package it.unipi.gamecritic.repositories.Review;

import org.springframework.data.mongodb.repository.MongoRepository;

import it.unipi.gamecritic.entities.Review;

public interface ReviewRepositoryMongoDB extends MongoRepository<Review, String>, CustomReviewRepository {
    
}
