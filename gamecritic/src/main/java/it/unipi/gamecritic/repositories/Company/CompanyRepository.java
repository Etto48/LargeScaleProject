package it.unipi.gamecritic.repositories.Company;

import java.util.List;

import org.springframework.stereotype.Component;

import it.unipi.gamecritic.entities.Company;

@Component
public class CompanyRepository implements CustomCompanyRepository {
    private final CompanyRepositoryMongoDB companyRepositoryMongoDB;

    public CompanyRepository(CompanyRepositoryMongoDB companyRepositoryMongoDB) {
        this.companyRepositoryMongoDB = companyRepositoryMongoDB;
    }

    public List<Company> findByDynamicAttribute(String attributeName, String attributeValue)
    {
        return companyRepositoryMongoDB.findByDynamicAttribute(attributeName, attributeValue);
    }
    
    public List<Company> search(String query)
    {
        return companyRepositoryMongoDB.search(query);
    }
}
