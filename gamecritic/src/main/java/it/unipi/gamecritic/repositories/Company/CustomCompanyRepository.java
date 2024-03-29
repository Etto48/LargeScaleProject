package it.unipi.gamecritic.repositories.Company;

import it.unipi.gamecritic.entities.Company;

import java.util.List;

public interface CustomCompanyRepository {
    List<Company> findByDynamicAttribute(String attributeName, String attributeValue);
    // List<DBObject> findCompaniesGames(String company);
    List<Company> search(String query);
    void updateTop3Games();
}
