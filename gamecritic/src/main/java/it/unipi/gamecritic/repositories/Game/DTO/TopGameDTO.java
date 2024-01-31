package it.unipi.gamecritic.repositories.Game.DTO;

public class TopGameDTO {
    public String name;
    public String image;
    public Float avg_score;

    public TopGameDTO(String name, Float avg_score, String image) {
        this.name = name;
        this.avg_score = avg_score;
        this.image = image;
    }

    public TopGameDTO() {
    }
}
