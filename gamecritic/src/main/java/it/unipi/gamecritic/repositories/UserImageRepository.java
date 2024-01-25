package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.UserImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserImageRepository extends MongoRepository<UserImage, String>, CustomUserImageRepository {

}