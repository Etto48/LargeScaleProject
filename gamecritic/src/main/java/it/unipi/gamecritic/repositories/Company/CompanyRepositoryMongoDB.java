package it.unipi.gamecritic.repositories.Company;

import it.unipi.gamecritic.entities.Company;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepositoryMongoDB extends MongoRepository<Company, String>, CustomCompanyRepository{
}
