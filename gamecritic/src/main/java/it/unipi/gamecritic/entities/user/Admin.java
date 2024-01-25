package it.unipi.gamecritic.entities.user;

import com.mongodb.DBObject;

public class Admin extends User {
    @Override
    public String getAccountType() {
        return "Admin";
    }

    public Admin() {

    }

    public Admin(DBObject dbObject) {
        super(dbObject);
    }
}
