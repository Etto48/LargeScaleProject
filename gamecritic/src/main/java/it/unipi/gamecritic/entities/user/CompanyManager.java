package it.unipi.gamecritic.entities.user;

import com.mongodb.DBObject;

public class CompanyManager extends User {
    public String company_name;
    @Override 
    public String getAccountType() {
        return "Company";
    }

    public CompanyManager() {

    }

    public CompanyManager(DBObject dbObject) {
        super(dbObject);
        this.company_name = (String) dbObject.get("company_name");
    }
}
