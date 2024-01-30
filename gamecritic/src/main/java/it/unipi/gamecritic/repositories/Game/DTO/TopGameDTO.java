package it.unipi.gamecritic.repositories.Game.DTO;

public class TopGameDTO {
    public String name;
    public Float avg_score;

    public TopGameDTO(String name, Float avg_score) {
        this.name = name;
        this.avg_score = avg_score;
    }

    public TopGameDTO() {
    }
}
