package it.unipi.gamecritic.repositories.UserImage;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.unipi.gamecritic.entities.UserImage;

@Component
public class UserImageRepository implements CustomUserImageRepository {
    private final UserImageRepositoryMongoDB userImageRepositoryMongoDB;

    public UserImageRepository(UserImageRepositoryMongoDB userImageRepositoryMongoDB) {
        this.userImageRepositoryMongoDB = userImageRepositoryMongoDB;
    }

    @Override
    public UserImage findImageByUsername(String username) {
        return userImageRepositoryMongoDB.findImageByUsername(username);
    }

    @Override
    @Async
    public void insertImage(UserImage userImage) {
        userImageRepositoryMongoDB.insertImage(userImage);
    }
    
}
