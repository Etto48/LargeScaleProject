package it.unipi.gamecritic.repositories.Game;

import com.mongodb.DBObject;

import it.unipi.gamecritic.entities.Comment;
import it.unipi.gamecritic.entities.Game;
import it.unipi.gamecritic.entities.Review;
import it.unipi.gamecritic.repositories.Game.DTO.TopGameDTO;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.List;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.DensifyOperation;
import org.springframework.data.mongodb.core.aggregation.DensifyOperation.Range;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CustomGameRepositoryImpl implements CustomGameRepository {
    private final MongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CustomGameRepositoryImpl.class);

    public CustomGameRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Game> search(String query){
        String escapedQuery = Pattern.quote(query);
        if (escapedQuery == null) {
            throw new IllegalArgumentException("The given query must not be null");
        }
        Criteria criteria = Criteria.where("Name").regex(escapedQuery, "i");
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
        logger.info("list of games: ");
        List<DBObject> game_objects = mongoTemplate.find(query, DBObject.class, "videogames");
        for(DBObject d : game_objects){
            logger.info(d.toString());
        }
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
        Aggregation a = Aggregation.newAggregation(
                Aggregation.stage("{\n" +
                "    $match: {\n" +
                "      \"reviews.date\": {\n" +
                "        $gte: \""+formattedDate+"\",\n" +
                "      },\n" +
                "    },\n" +
                "  }"),
                Aggregation.stage("{\n" +
                        "    $group: {\n" +
                        "      _id: \"$_id\",\n" +
                        "      Name: {\n" +
                        "        $first: \"$Name\",\n" +
                        "      },\n" +
                        "      HotReviewCount: {\n" +
                        "        $sum: {\n" +
                        "          $size: {\n" +
                        "            $filter: {\n" +
                        "              input: \"$reviews\",\n" +
                        "              as: \"review\",\n" +
                        "              cond: {\n" +
                        "                $gte: [\n" +
                        "                  \"$$review.date\",\n" +
                        "                  \"2023-07-21\",\n" +
                        "                ],\n" +
                        "              },\n" +
                        "            },\n" +
                        "          },\n" +
                        "        },\n" +
                        "      },\n" +
                        "      reviews: {\n" +
                        "        $push: \"$reviews\",\n" +
                        "      },\n" +
                        "      allAttributes: {\n" +
                        "        $mergeObjects: \"$$ROOT\",\n" +
                        "      },\n" +
                        "    },\n" +
                        "  }"),
                Aggregation.stage("{\n" +
                        "    $sort:\n" +
                        "      {\n" +
                        "        HotReviewCount: -1,\n" +
                        "        Name: 1             \n"+
                        "      },\n" +
                        "  }"),
                Aggregation.stage("{\n" +
                        "    $replaceRoot: {\n" +
                        "      newRoot: {\n" +
                        "        $mergeObjects: [\n" +
                        "          \"$allAttributes\",\n" +
                        "          {\n" +
                        "            reviews: {\n" +
                        "              $filter: {\n" +
                        "                input: \"$reviews\",\n" +
                        "                as: \"review\",\n" +
                        "                cond: {\n" +
                        "                  $gte: [\n" +
                        "                    \"$$review.date\",\n" +
                        "                    \"2023-07-21\",\n" +
                        "                  ],\n" +
                        "                },\n" +
                        "              },\n" +
                        "            },\n" +
                        "          },\n" +
                        "        ],\n" +
                        "      },\n" +
                        "    },\n" +
                        "  }"),
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
    public void addGame(Document game){
        if(game == null) {
            throw new IllegalArgumentException("The given game must not be null");
        }
        mongoTemplate.save(game,"videogames");
    }

    @Override
    public void editGame(Document game, String id) {
        if(game == null) {
            throw new IllegalArgumentException("The given game must not be null");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        logger.info("name of game "+game.get("Name"));
        Update update = new Update();
        for (String key : game.keySet()){
            logger.info(key);
            update.set(key,game.get(key));
        }
        mongoTemplate.updateFirst(query,update,"videogames");
    }

    @Override
    public List<TopGameDTO> topGamesByAverageScore(Integer months, String companyName, Integer limit) {
        if (months == null || months < 1) {
            throw new IllegalArgumentException("The given months must not be null nor less than 1");
        }
        Calendar d = Calendar.getInstance();
        Integer this_year = d.get(Calendar.YEAR);
        Integer this_month = d.get(Calendar.MONTH) + 1;
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
                Criteria.where("reviews.score").exists(true)
            )),
            Aggregation.group("Name").avg("reviews.score").as("averageScore").count().as("reviewCount").first("img").as("img"),
            Aggregation.sort(Sort.Direction.DESC, "averageScore").and(Sort.Direction.DESC, "reviewCount"),
            Aggregation.limit(limit)
        );
        List<DBObject> games_dbos = mongoTemplate.aggregate(aggregation, "videogames", DBObject.class).getMappedResults();
        List<TopGameDTO> games = games_dbos.stream().map(game -> new TopGameDTO(game.get("_id").toString(), ((Double)game.get("averageScore")).floatValue(), game.get("img") != null?game.get("img").toString():null)).toList();
        return games;
    }

    @Override
    public List<Float> globalScoreDistribution()
    {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.unwind("reviews"),
            Aggregation.match(new Criteria().andOperator(
                Criteria.where("reviews.score").exists(true),
                Criteria.where("reviews.score").gt(0)
            )),
            Aggregation.group("reviews.score").count().as("count"),
            DensifyOperation.builder().densify("_id").range(Range.bounded(1, 11).incrementBy(1)).build(),
            Aggregation.sort(Sort.Direction.ASC, "_id")
        );
        List<DBObject> games_dbos = mongoTemplate.aggregate(aggregation, "videogames", DBObject.class).getMappedResults();
        List<Float> games = games_dbos.stream().map(game -> 
        {
            if(game.get("count") != null)
            {
                return ((Integer)game.get("count")).floatValue();
            }
            else
            {
                return 0f;
            }
        }).toList();
        return games;
    }

    @Override
    public List<Float> companyScoreDistribution(String companyName)
    {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(new Criteria().orOperator(
                Criteria.where("Developers").is(companyName),
                Criteria.where("Publishers").is(companyName),
                Criteria.where("Developers").elemMatch(Criteria.where("$eq").is(companyName)),
                Criteria.where("Publishers").elemMatch(Criteria.where("$eq").is(companyName))
            )),
            Aggregation.unwind("reviews"),
            Aggregation.match(new Criteria().andOperator(
                Criteria.where("reviews.score").exists(true),
                Criteria.where("reviews.score").gt(0)
            )),
            Aggregation.group("reviews.score").count().as("count"),
            DensifyOperation.builder().densify("_id").range(Range.bounded(1, 11).incrementBy(1)).build(),
            Aggregation.sort(Sort.Direction.ASC, "_id")
        );
        List<DBObject> games_dbos = mongoTemplate.aggregate(aggregation, "videogames", DBObject.class).getMappedResults();
        List<Float> games = games_dbos.stream().map(game -> 
        {
            if(game.get("count") != null)
            {
                return ((Integer)game.get("count")).floatValue();
            }
            else
            {
                return 0f;
            }
        }).toList();
        return games;
    }

    @Override
    public void updateTop3ReviewsByLikes()
    {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.stage(
                "{\n" + //
                "  $group: {\n" + //
                "    _id: \"$game\",\n" + //
                "    Top3ReviewsByLikes: {\n" + //
                "      $topN: {\n" + //
                "        n: 3,\n" + //
                "        output: {\n" + //
                "          _id: \"$_id\",\n" + //
                "          game: \"$game\",\n" + //
                "          quote: \"$quote\",\n" + //
                "          author: \"$author\",\n" + //
                "          date: \"$date\",\n" + //
                "          score: \"$score\",\n" + //
                "          likes: \"$likes\",\n" + //
                "        },\n" + //
                "        sortBy: {\n" + //
                "          likes: -1,\n" + //
                "        },\n" + //
                "      },\n" + //
                "    },\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $set: {\n" + //
                "    Name: \"$_id\",\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $project: {\n" + //
                "    _id: 0,\n" + //
                "    Name: 1,\n" + //
                "    Top3ReviewsByLikes: 1,\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $merge: {\n" + //
                "    into: \"videogames\",\n" + //
                "    on: \"Name\",\n" + //
                "    whenMatched: \"merge\",\n" + //
                "    whenNotMatched: \"discard\",\n" + //
                "  },\n" + //
                "}"
            )
        ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        Instant start = Instant.now();
        mongoTemplate.aggregate(aggregation, "reviews", DBObject.class).getMappedResults();
        logger.info("Finished updating top 3 reviews by likes for all games in " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + " ms");
    }
}

