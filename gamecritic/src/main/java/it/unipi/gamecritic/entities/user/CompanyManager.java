package it.unipi.gamecritic.entities.user;

import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.mapping.Field;

public class CompanyManager extends User {
    @Field("company_name")
    public String company_name;
    @Override 
    public String getAccountType() {
        return "Company";
    }

    public CompanyManager() {

    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getCompany_name() {
        return company_name;
    }

    public CompanyManager(DBObject dbObject) {
        super(dbObject);
        this.company_name = (String) dbObject.get("company_name");
    }
}
