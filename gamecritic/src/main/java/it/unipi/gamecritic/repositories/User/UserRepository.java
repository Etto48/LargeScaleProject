// UserRepository

package it.unipi.gamecritic.repositories.User;

import it.unipi.gamecritic.entities.user.User;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, String>, CustomUserRepository {

}