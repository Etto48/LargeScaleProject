package it.unipi.gamecritic.repositories.Game;

import com.mongodb.DBObject;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Game.DTO.TopGameDTO;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        return game_objects.stream().map(Game::new).toList();
    }
    @Override
    public List<Game> findByDynamicAttribute(String attributeName, String attributeValue) {
        if(attributeName == null || attributeValue == null) {
            throw new IllegalArgumentException("The given attribute name or value must not be null");
        }
        Query query = new Query(Criteria.where(attributeName).is(attributeValue));
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        return game_objects.stream().map(Game::new).toList();
    }

    @Override
    public List<Game> findLatest(Integer offset){
        Query query = new Query();
        query.addCriteria(Criteria.where("Released.Release Date").ne("Undated"));
        query.with(Sort.by(Sort.Order.desc("Released.Release Date"))).skip(offset).limit(10);
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        return game_objects.stream().map(Game::new).toList();
    }

    @Override
    public List<Game> findBest(Integer offset) {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("user_review"), Sort.Order.desc("reviewCount"))).skip(offset).limit(10);
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        return game_objects.stream().map(Game::new).toList();
    }

    @Override
    public List<Game> findVideoGamesOfCompany(String companyName) {
        Query query = new Query();
        query.addCriteria(
            new Criteria().orOperator(
                Criteria.where("Developers").is(companyName),
                Criteria.where("Publishers").is(companyName),
                Criteria.where("Developers").elemMatch(new Criteria().is(companyName)),
                Criteria.where("Publishers").elemMatch(new Criteria().is(companyName))
            )
        );
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        return game_objects.stream().map(Game::new).toList();
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
        List<DBObject> game_objects = mongoTemplate.aggregate(a, "videogames", DBObject.class).getMappedResults();
        return game_objects.stream().map(Game::new).toList();
    }

    @Override
    @Async
    public void deleteGame(String name) {
        if(name == null) {
            throw new IllegalArgumentException("The given name must not be null");
        }
        Query gamesQuery = new Query(Criteria.where("Name").is(name));
        Query reviewsQuery = new Query(Criteria.where("game").is(name));

        mongoTemplate.remove(gamesQuery, Game.class, "videogames");
        mongoTemplate.find(reviewsQuery,Review.class, "reviews").forEach(review -> {
            Query commentsQuery = new Query(Criteria.where("reviewId").is(review.id));
            mongoTemplate.remove(commentsQuery, Comment.class, "comments");
        });
        mongoTemplate.remove(reviewsQuery, Review.class, "reviews");
    }

    @Override
    public List<TopGameDTO> topGamesByAverageScore(Integer months, String companyName) {
        if (months == null || months < 1) {
            throw new IllegalArgumentException("The given months must not be null nor less than 1");
        }
        Calendar now = Calendar.getInstance();
        DateFormat date = new SimpleDateFormat("yyyy");
        Integer this_year = Integer.parseInt(date.format(now));
        Integer this_month = now.get(Calendar.MONTH) + 1;
        String regex = "^(";
        for(int i = 0; i < months; i++)
        {
            Integer month = this_month - i;
            Integer year = this_year;
            if(month <= 0)
            {
                month += 12;
                year -= 1;
            }
            regex += year.toString() + "-" + String.format("%02d", month);
            if(i != months - 1)
            {
                regex += "|";
            }
        }
        regex += ")";

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(new Criteria().orOperator(
                Criteria.where("Developers").is(companyName),
                Criteria.where("Publishers").is(companyName),
                Criteria.where("Developers").elemMatch(new Criteria().is(companyName)),
                Criteria.where("Publishers").elemMatch(new Criteria().is(companyName))
            )),
            Aggregation.unwind("reviews"),
            Aggregation.match(new Criteria().andOperator(
                Criteria.where("reviews.date").regex(regex),
                Criteria.where("reviews.score").ne(null)
            )),
            Aggregation.group("Name").avg("reviews.score").as("averageScore")
        );
        List<DBObject> games_dbos = mongoTemplate.aggregate(aggregation, "games", DBObject.class).getMappedResults();
        List<TopGameDTO> games = games_dbos.stream().map(game -> new TopGameDTO(game.get("_id").toString(), (Float)game.get("averageScore"))).toList();
        return games;
    }
}

