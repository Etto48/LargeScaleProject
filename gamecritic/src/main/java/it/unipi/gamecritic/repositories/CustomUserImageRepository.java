package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.UserImage;

public interface CustomUserImageRepository {
    /// Returns null if no image is found
    UserImage findImageByUsername(String username);
    void insertImage(UserImage userImage);
}
