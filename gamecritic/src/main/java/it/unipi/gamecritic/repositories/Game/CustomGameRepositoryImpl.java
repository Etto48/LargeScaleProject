package it.unipi.gamecritic.repositories.Game;

import com.mongodb.DBObject;

import it.unipi.gamecritic.entities.Game;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.List;
import java.util.Vector;

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
    public List<Game> search(String query){
        if(query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        Criteria criteria = Criteria.where("Name").regex(query, "i");
        Query q = new Query(criteria).limit(10).with(Sort.by(Sort.Order.desc("reviewCount")));

        List<DBObject> game_objects = mongoTemplate.find(q, DBObject.class, "videogames");
        List<Game> games = game_objects.stream().map(
            game_object -> {
                return new Game(game_object);
            }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }
    @Override
    public List<Game> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        List<Game> games = game_objects.stream().map(
            game_object -> {
                return new Game(game_object);
            }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }

    @Override
    public List<Game> findLatest(Integer offset){
        Query query = new Query();
        query.addCriteria(Criteria.where("Released.Release Date").ne("Undated"));
        query.with(Sort.by(Sort.Order.desc("Released.Release Date"))).skip(offset).limit(10);
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        List<Game> games = game_objects.stream().map(
                game_object -> {
                    return new Game(game_object);
                }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }

    @Override
    public List<Game> findBest(Integer offset) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("user_review"), Sort.Order.desc("reviewCount"))).skip(offset).limit(10);
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        List<Game> games = game_objects.stream().map(
                game_object -> {
                    return new Game(game_object);
                }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }

    @Override
    public List<Game> findVideoGamesOfCompany(String companyName) {
        Query query = new Query();
        query.addCriteria(
            new Criteria().orOperator(
                Criteria.where("Developers").elemMatch(Criteria.where("$eq").is(companyName)),
                Criteria.where("Publishers").elemMatch(Criteria.where("$eq").is(companyName))
            )
        );
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        List<Game> games = game_objects.stream().map(
                game_object -> {
                    return new Game(game_object);
                }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }

    @Override
    public List<Game> findVideoGamesWithMostReviewsLastMonth(Integer offset, String latest) {
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
        List<Game> games = result.stream().map(
                game_object -> {
                    return new Game(game_object);
                }
        ).collect(Vector::new, Vector::add, Vector::addAll);
        return games;
    }
}

