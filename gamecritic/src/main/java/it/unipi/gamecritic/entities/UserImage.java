package it.unipi.gamecritic.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class UserImage {
    @Id
    public ObjectId id;
    public String username;
    @Field("image")
    public String b64_image;

    public UserImage(String username, byte[] b64_image) {
        this.username = username;
        this.b64_image = java.util.Base64.getEncoder().encodeToString(b64_image);
    }

    public UserImage() {
    }

    public byte[] getImage() {
        return java.util.Base64.getDecoder().decode(b64_image);
    }
}
