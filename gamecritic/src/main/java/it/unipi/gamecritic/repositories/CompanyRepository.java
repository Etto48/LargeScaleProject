package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String>, CustomCompanyRepository{
}
