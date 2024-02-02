package it.unipi.gamecritic.repositories.Company;

import it.unipi.gamecritic.entities.Company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.regex.Pattern;

public class CustomCompanyRepositoryImpl implements CustomCompanyRepository{
    private final MongoTemplate mongoTemplate;
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CustomCompanyRepositoryImpl.class);

    public CustomCompanyRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public List<Company> findByDynamicAttribute(String attributeName, String attributeValue) {
        if (attributeName == null || attributeValue == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        return mongoTemplate.find(query, Company.class, "companies");
    }
    @Override
    public List<Company> search(String query){
        String escapedQuery = Pattern.quote(query);
        if (escapedQuery == null) {
            throw new IllegalArgumentException("The given query must not be null");
        }
        Criteria criteria = Criteria.where("Name").regex(escapedQuery, "i");
        Query q = new Query(criteria).limit(10);
        return mongoTemplate.find(q, Company.class, "companies");
    }

}
