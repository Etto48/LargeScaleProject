package it.unipi.gamecritic.repositories;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Accumulators.mergeObjects;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.In.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.BooleanOperators.And.and;


import java.util.Arrays;
import java.util.List;
import com.mongodb.DBObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class CustomGameRepositoryImpl implements CustomGameRepository {
    private final MongoTemplate mongoTemplate;

    public CustomGameRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DBObject> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            return null;
        }
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
        query.with(Sort.by(Sort.Order.desc("user_review"), Sort.Order.asc("Name"))).skip(offset).limit(10);
        return mongoTemplate.find(query, DBObject.class, "videogames");
    }
    @Override
    public List<DBObject> findVideoGamesWithMostReviewsLastMonth(Integer offset) {
        LocalDate currentDate = LocalDate.now();

        // Subtract 6 months from the current date
        LocalDate sixMonthsAgo = currentDate.minusMonths(6);

        // Format the date as a string in the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = sixMonthsAgo.format(formatter);

        // Print the result
        System.out.println("Date of 6 months ago: " + formattedDate);

        Aggregation a = Aggregation.newAggregation(Aggregation.stage("{\n" +
                "    $unwind: \"$reviews\",\n" +
                "  }"),
                Aggregation.stage("{\n" +
                "    $match: {\n" +
                "      \"reviews.date\": {\n" +
                "        $gte: \"2023-07-21\",\n" +
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
        System.out.println("woinwoirgioreoin");
        List<DBObject> result = mongoTemplate.aggregate(a, "videogames", DBObject.class).getMappedResults();
        System.out.println("after");
        return result;

    }
}

