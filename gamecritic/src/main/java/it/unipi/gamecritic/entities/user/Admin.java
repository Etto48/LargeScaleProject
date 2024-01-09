package it.unipi.gamecritic.entities.user;

public class Admin extends User {
    @Override
    public String getAccountType() {
        return "Admin";
    }
}
