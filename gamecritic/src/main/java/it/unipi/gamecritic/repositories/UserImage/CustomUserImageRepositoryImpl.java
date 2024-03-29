package it.unipi.gamecritic.repositories.UserImage;

import it.unipi.gamecritic.entities.UserImage;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;

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
    @Async
    public void insertImage(UserImage userImage) {
        if (userImage == null) {
            throw new IllegalArgumentException("The given username or image must not be null");
        }
        Query query = new Query(Criteria.where("username").is(userImage.username));
        Update update = new Update().set("image", userImage.b64_image);
        mongoTemplate.upsert(query, update, "user_images");
    }
}
