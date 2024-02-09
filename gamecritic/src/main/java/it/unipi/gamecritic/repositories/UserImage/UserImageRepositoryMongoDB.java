package it.unipi.gamecritic.repositories.UserImage;

import it.unipi.gamecritic.entities.UserImage;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserImageRepositoryMongoDB extends MongoRepository<UserImage, String>, CustomUserImageRepository {

}