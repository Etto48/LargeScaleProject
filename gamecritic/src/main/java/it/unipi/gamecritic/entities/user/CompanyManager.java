package it.unipi.gamecritic.entities.user;

public class CompanyManager extends User {
    @Override 
    public String getAccountType() {
        return "Company";
    }
}
