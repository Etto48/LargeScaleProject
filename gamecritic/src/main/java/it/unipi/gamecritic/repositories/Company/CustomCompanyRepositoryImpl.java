package it.unipi.gamecritic.repositories.Company;

import it.unipi.gamecritic.entities.Company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBObject;

import java.time.Instant;
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

    @Override
    public void updateTop3Games() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.stage(
                "{\n" + //
                "  $addFields: {\n" + //
                "    Publishers: {\n" + //
                "      $cond: {\n" + //
                "        if: {\n" + //
                "          $ifNull: [\"$Publishers\", false],\n" + //
                "        },\n" + //
                "        else: null,\n" + //
                "        then: {\n" + //
                "          $cond: {\n" + //
                "            if: {\n" + //
                "              $eq: [\n" + //
                "                {\n" + //
                "                  $type: \"$Publishers\",\n" + //
                "                },\n" + //
                "                \"array\",\n" + //
                "              ],\n" + //
                "            },\n" + //
                "            then: \"$Publishers\",\n" + //
                "            else: [\"$Publishers\"],\n" + //
                "          },\n" + //
                "        },\n" + //
                "      },\n" + //
                "    },\n" + //
                "    Developers: {\n" + //
                "      $cond: {\n" + //
                "        if: {\n" + //
                "          $ifNull: [\"$Developers\", false],\n" + //
                "        },\n" + //
                "        else: null,\n" + //
                "        then: {\n" + //
                "          $cond: {\n" + //
                "            if: {\n" + //
                "              $eq: [\n" + //
                "                {\n" + //
                "                  $type: \"$Developers\",\n" + //
                "                },\n" + //
                "                \"array\",\n" + //
                "              ],\n" + //
                "            },\n" + //
                "            then: \"$Developers\",\n" + //
                "            else: [\"$Developers\"],\n" + //
                "          },\n" + //
                "        },\n" + //
                "      },\n" + //
                "    },\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $addFields: {\n" + //
                "    companies: {\n" + //
                "      $setUnion: [\"$Developers\", \"$Publishers\"],\n" + //
                "    },\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $unwind: {\n" + //
                "    path: \"$companies\",\n" + //
                "    preserveNullAndEmptyArrays: false,\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $group: {\n" + //
                "    _id: \"$companies\",\n" + //
                "    Top3Games: {\n" + //
                "      $topN: {\n" + //
                "        n: 3,\n" + //
                "        output: {\n" + //
                "          Name: \"$Name\",\n" + //
                "          Description: \"$Description\",\n" + //
                "          img: \"$img\",\n" + //
                "          user_review: \"$user_review\",\n" + //
                "        },\n" + //
                "        sortBy: {\n" + //
                "          user_review: -1,\n" + //
                "          reviewCount: -1,\n" + //
                "        },\n" + //
                "      },\n" + //
                "    },\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $addFields:\n" + //
                "    {\n" + //
                "      Name: \"$_id\",\n" + //
                "    },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $project: {\n" + //
                "    _id: 0,\n" + //
                "    Name: 1,\n" + //
                "    Top3Games: 1,\n" + //
                "  },\n" + //
                "}"
            ),
            Aggregation.stage(
                "{\n" + //
                "  $merge: {\n" + //
                "    into: \"companies\",\n" + //
                "    on: \"Name\",\n" + //
                "    whenMatched: \"merge\",\n" + //
                "    whenNotMatched: \"discard\",\n" + //
                "  },\n" + //
                "}"
            )
        ).withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        Instant start = Instant.now();
        mongoTemplate.aggregate(aggregation, "videogames", DBObject.class).getRawResults();
        logger.info("Finished updating top 3 games by user_score for all companies in " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + " ms");
    }
}
