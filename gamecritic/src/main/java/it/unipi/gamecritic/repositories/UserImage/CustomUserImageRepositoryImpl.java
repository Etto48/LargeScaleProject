package it.unipi.gamecritic.repositories.UserImage;

import it.unipi.gamecritic.entities.UserImage;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CustomUserImageRepositoryImpl implements CustomUserImageRepository{
    private final MongoTemplate mongoTemplate;
    public CustomUserImageRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserImage findImageByUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("The given username must not be null");
        }
        Criteria criteria = Criteria.where("username").is(username);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, UserImage.class, "user_images");
    }

    @Override
    public void insertImage(UserImage userImage) {
        if (userImage == null) {
            throw new IllegalArgumentException("The given username or image must not be null");
        }
        mongoTemplate.insert(userImage, "user_images");
    }
}
