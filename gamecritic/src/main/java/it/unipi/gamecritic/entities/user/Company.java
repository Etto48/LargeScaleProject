package it.unipi.gamecritic.entities.user;

public class Company extends User {
    @Override 
    public String getAccountType() {
        return "Company";
    }
}
