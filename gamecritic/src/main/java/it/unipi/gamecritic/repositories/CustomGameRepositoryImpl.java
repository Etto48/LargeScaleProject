package it.unipi.gamecritic.repositories;

import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomGameRepositoryImpl implements CustomGameRepository {
    private final MongoTemplate mongoTemplate;

    public CustomGameRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DBObject> search(String query){
        if(query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        Criteria criteria = Criteria.where("Name").regex(query, "i");
        Query q = new Query(criteria).limit(10).with(Sort.by(Sort.Order.desc("reviewCount")));

        return mongoTemplate.find(q, DBObject.class, "videogames");
    }
    @Override
    public List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            return null;
        }
        System.out.println("in game findDynamic");
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));

        return mongoTemplate.find(query, DBObject.class, "videogames");
    }

    @Override
    public List<DBObject> findLatest(Integer offset){
        Query query = new Query();
        query.addCriteria(Criteria.where("Released.Release Date").ne("Undated"));
        query.with(Sort.by(Sort.Order.desc("Released.Release Date"))).skip(offset).limit(10);
        return mongoTemplate.find(query, DBObject.class, "videogames");
    }

    @Override
    public List<DBObject> findBest(Integer offset) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("user_review"), Sort.Order.desc("reviewCount"))).skip(offset).limit(10);
        return mongoTemplate.find(query, DBObject.class, "videogames");
    }
    @Override
    public List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest) {
        LocalDate currentDate = LocalDate.now();
        LocalDate ago;
        if (latest.equals("month")){
            ago = currentDate.minusMonths(6);
            // per ora metto 6 per testing
        }
        else {
            ago = currentDate.minusMonths(6);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = ago.format(formatter);
        Aggregation a = Aggregation.newAggregation(Aggregation.stage("{\n" +
                "    $unwind: \"$reviews\",\n" +
                "  }"),
                Aggregation.stage("{\n" +
                "    $match: {\n" +
                "      \"reviews.date\": {\n" +
                "        $gte: \""+formattedDate+"\",\n" +
                "      },\n" +
                "    },\n" +
                "  }"),
                Aggregation.stage("{\n" +
                        "    $group:\n" +
                        "      {\n" +
                        "        _id: \"$_id\",\n" +
                        "        Name: {\n" +
                        "          $first: \"$Name\",\n" +
                        "        },\n" +
                        "        HotReviewCount: {\n" +
                        "          $sum: 1,\n" +
                        "        },\n" +
                        "        reviews: {\n" +
                        "          $push: \"$reviews\",\n" +
                        "        },\n" +
                        "        allAttributes: {\n" +
                        "          $mergeObjects: \"$$ROOT\",\n" +
                        "        },\n" +
                        "      },\n" +
                        "  }"),
                Aggregation.stage("{\n" +
                        "    $sort:\n" +
                        "      {\n" +
                        "        HotReviewCount: -1,\n" +
                        "        Name: 1             \n"+
                        "      },\n" +
                        "  }"),
                Aggregation.stage("{" +
                        "       $replaceRoot:\n" +
                        "      {\n" +
                        "        newRoot: {\n" +
                        "          $mergeObjects: [\n" +
                        "            \"$allAttributes\",\n" +
                        "            {\n" +
                        "              reviews: \"$reviews\",\n" +
                        "            },\n" +
                        "          ],\n" +
                        "        },\n" +
                        "      }}"),
                Aggregation.stage("{" +
                        "$skip: "+offset+" }"),
                Aggregation.stage("{\n" +
                        "    $limit:\n"+
                        "      10,\n" +
                        "  }")
        );
        List<DBObject> result = mongoTemplate.aggregate(a, "videogames", DBObject.class).getMappedResults();
        return result;

    }
}

