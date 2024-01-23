package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Company;

import java.util.List;

public interface CustomCompanyRepository {
    List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue);
    List<Company> search(String query);
}
