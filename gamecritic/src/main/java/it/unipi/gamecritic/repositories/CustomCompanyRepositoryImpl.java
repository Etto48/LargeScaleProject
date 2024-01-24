package it.unipi.gamecritic.repositories;

import it.unipi.gamecritic.entities.Company;
import it.unipi.gamecritic.entities.Game;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
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
    /*
    @Override
    public List<DBObject> findCompaniesGames(String company) {
        if(company == null) {
            return null;
        }
        System.out.println("in company findGames");
        Criteria expression1 = Criteria.where("Publishers").is(company);
        Criteria expression2 = Criteria.where("Developers").is(company);
        // Build the $or query
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(expression1, expression2));
        return mongoTemplate.find(query, DBObject.class, "videogames");
    }

     */
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
