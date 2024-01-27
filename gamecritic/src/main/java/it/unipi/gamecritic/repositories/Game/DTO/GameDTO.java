package it.unipi.gamecritic.repositories.Game.DTO;

import it.unipi.gamecritic.entities.Game;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Game")
public class GameDTO {
    @Id
    @GeneratedValue
    public Long id;
    @Property("name")
    public String name;

    public GameDTO(String name){
        this.name = name;
    }
}
