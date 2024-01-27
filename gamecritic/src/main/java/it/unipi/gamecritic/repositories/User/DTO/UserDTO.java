package it.unipi.gamecritic.repositories.User.DTO;

import java.util.UUID;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("User")
public class UserDTO {
    @Id
    @GeneratedValue
    public UUID id;
    @Property("username")
    public String username;
    public  UserDTO(String username){
        this.username = username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
