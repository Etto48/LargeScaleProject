package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.Company;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Vector;

public class CustomCompanyRepositoryImpl implements CustomCompanyRepository{
    private final MongoTemplate mongoTemplate;

    public CustomCompanyRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public List<Company> findByDynamicAttribute(String attributeName, String attributeValue) {
        if (attributeName == null || attributeValue == null) {
            return null;
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        return mongoTemplate.find(query, Company.class, "companies");
    }
    @Override
    public List<Company> search(String query){
        if (query == null) {
            return new Vector<>();
        }
        Criteria criteria = Criteria.where("Name").regex(query, "i");
        Query q = new Query(criteria).limit(10);
        return mongoTemplate.find(q, Company.class, "companies");
    }
}
