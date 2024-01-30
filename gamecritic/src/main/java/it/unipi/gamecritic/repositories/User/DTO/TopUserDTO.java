package it.unipi.gamecritic.repositories.User.DTO;

public class TopUserDTO {
    public String username;
    public Integer score;

    public TopUserDTO(String username, Integer score) {
        this.username = username;
        this.score = score;
    }

    public TopUserDTO() {
    }
}
