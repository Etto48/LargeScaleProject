package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;

import java.util.List;

public interface CustomCompanyRepository {
    List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue);
    List<DBObject> search(String query);
}
