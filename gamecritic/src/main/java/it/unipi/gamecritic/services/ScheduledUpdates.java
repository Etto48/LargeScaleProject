package it.unipi.gamecritic.services;

import com.mongodb.DBObject;
import it.unipi.gamecritic.entities.Company;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledUpdates {
    private final MongoTemplate mongoTemplate;

    public ScheduledUpdates(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    // Run on the first day of every month at 12:00 AM
    @Scheduled(cron = "0 0 0 1 * ?")
    public void updateTop3Games() {
        System.out.println("pre bulkops");
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Company.class, "companies");
        System.out.println("post bulkops");
        for (Company company : mongoTemplate.findAll(Company.class, "companies")) {
            System.out.println("inside findall cycle");
            Query query = new Query();
            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("Publishers").is(company.name),
                    Criteria.where("Developers").is(company.name)
            );
            query.addCriteria(criteria);
            query.with(Sort.by(Sort.Order.desc("user_review"), Sort.Order.desc("reviewCount"))).limit(3);
            List<DBObject> l = mongoTemplate.find(query, DBObject.class, "videogames");
            System.out.println("size of list of top 3: " + l.size());
            List<Document> games = new ArrayList<>();
            for (DBObject o : l) {
                Document g = new Document();
                System.out.println("o: " + o.toString());
                g.put("Name", o.get("Name").toString());
                if (o.get("img") != null) {
                    g.put("img", o.get("img").toString());
                } else {
                    g.put("img", null);
                }
                if (o.get("user_review") != null) {
                    g.put("user_review", Float.valueOf(o.get("user_review").toString()));
                } else {
                    g.put("user_review", null);
                }
                games.add(g);
            }
            Update update = new Update().set("Top3Games", games);
            System.out.println("company id: " + company._id);
            System.out.println("list: " + games.toString());
            bulkOps.updateOne(Query.query(Criteria.where("_id").is(company._id)), update);
        }
        bulkOps.execute();
    }

}
