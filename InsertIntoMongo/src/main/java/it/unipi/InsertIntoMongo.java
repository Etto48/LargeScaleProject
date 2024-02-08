package it.unipi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class InsertIntoMongo {

    private static BigInteger gameIdCounter = new BigInteger("1");
    private static BigInteger reviewIdCounter = new BigInteger("1");
    private static BigInteger commentIdCounter = new BigInteger("1");
    private static BigInteger companyIdCounter = new BigInteger("1");
    private static BigInteger userIdCounter = new BigInteger("1");
    private static BigInteger userImageIdCounter = new BigInteger("1");

    private static String gamesPath = "./dataset/games/commented_games.json";
    private static String reviewsInfoPath = "./dataset/reviews/reviews_info.json";
    private static String companiesPath = "./dataset/companies/combined_companies.json";
    private static String usersPath = "./dataset/users/users.json";
    private static String cmAndAdminsPath = "./dataset/users/cm_and_admins.json";
    private static String configPath = "./InsertIntoMongo/config.json";
    private static String dbUri = "mongodb://localhost:27017";
    private static final Logger logger = LogManager.getLogger(InsertIntoMongo.class);
    public static void main(String[] args) {
        
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Load JSON config file
            JsonNode config = objectMapper.readTree(new File(configPath));
            JsonNode games = config.get("gamesPath");
            if (games != null)
            {
                gamesPath = games.asText();
            }
            JsonNode companies = config.get("companiesPath");
            if (companies != null)
            {
                companiesPath = companies.asText();
            }
            JsonNode users = config.get("usersPath");
            if (users != null)
            {
                usersPath = users.asText();
            }
            JsonNode dbUriNode = config.get("dbUri");
            if (dbUriNode != null)
            {
                dbUri = dbUriNode.asText();
            }
            logger.info("Loaded config from file");
        }
        catch (IOException e)
        {
            logger.warn("Could not load config from file, using default values");
        }
        try {
            // Connect to MongoDB (assuming it's running locally on the default port)
            MongoClientURI uri = new MongoClientURI(dbUri);

            MongoClient mongoClient = new MongoClient(uri);
            logger.info("Connected to MongoDB");
            MongoDatabase database = mongoClient.getDatabase("GameCritic");
            database.drop();

            // Load JSON data from a file
            JsonNode gamesJson = objectMapper.readTree(new File(gamesPath));
            JsonNode reviewsInfoJson = objectMapper.readTree(new File(reviewsInfoPath));
            JsonNode companiesJson = objectMapper.readTree(new File(companiesPath));
            JsonNode usersJson = objectMapper.readTree(new File(usersPath));
            JsonNode cmAndAdminsJson = objectMapper.readTree(new File(cmAndAdminsPath));
            logger.info("Loaded JSON data");

            // Insert data into the "videogames" collection
            MongoCollection<Document> videoGamesCollection = database.getCollection("videogames");
            videoGamesCollection.createIndex(new Document("Name", 1), new IndexOptions().unique(true));
            insertDynamicData(gamesJson, videoGamesCollection);
            logger.info("Collection \"videogames\" created");

            // Insert data into the "reviews" collection and the "comments" collection
            MongoCollection<Document> reviewsCollection = database.getCollection("reviews");
            reviewsCollection.createIndex(new Document("author", 1).append("game", 1), new IndexOptions().unique(true));
            reviewsCollection.createIndex(new Document("game", 1));
            reviewsCollection.createIndex(new Document("author", 1));
            MongoCollection<Document> commentsCollection = database.getCollection("comments");
            commentsCollection.createIndex(new Document("reviewId", 1));
            insertReviewsAndComments(gamesJson, reviewsInfoJson, reviewsCollection, commentsCollection);
            logger.info("Collection \"reviews\" created");
            logger.info("Collection \"comments\" created");

            // Insert data into the "companies" collection
            MongoCollection<Document> companiesCollection = database.getCollection("companies");
            companiesCollection.createIndex(new Document("Name", 1), new IndexOptions().unique(true));
            insertCompanies(companiesJson, companiesCollection);
            logger.info("Collection \"companies\" created");

            // Insert data into the "users" collection
            MongoCollection<Document> usersCollection = database.getCollection("users");
            usersCollection.createIndex(new Document("username", 1), new IndexOptions().unique(true));
            insertUsers(usersJson, usersCollection);
            insertUsers(cmAndAdminsJson, usersCollection);
            logger.info("Collection \"users\" created");

            // Insert data into the "user_images" collection
            MongoCollection<Document> userImagesCollection = database.getCollection("user_images");
            userImagesCollection.createIndex(new Document("username", 1), new IndexOptions().unique(true));
            insertUserImages(usersJson, userImagesCollection);
            insertUserImages(cmAndAdminsJson, userImagesCollection);
            logger.info("Collection \"user_images\" created");

            
            // Close MongoDB connection
            mongoClient.close();
            logger.info("Successfully inserted data into MongoDB");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertDynamicData(JsonNode jsonNode, MongoCollection<Document> collection) throws ParseException {
        List<Document> documents = new ArrayList<>();
        int howManyReviews = 0;

        for (JsonNode gameNode : jsonNode) {
            howManyReviews = 0;
            Document document = new Document("_id", idFromBignum(gameIdCounter));
            gameIdCounter = gameIdCounter.add(new BigInteger("1"));
            // Iterate through the dynamic attributes before "reviews"
            Iterator<String> fieldNames = gameNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();

                JsonNode fieldValue = gameNode.get(fieldName);
                if (fieldName.equals("Official Site")){
                    continue;
                }
                if (fieldName.equals("reviews")) {
                    List<Object> arrayValues = new ArrayList<>();

                    for (JsonNode arrayNode : fieldValue) {
                        Document nestedDocument = new Document();
                        if (arrayNode.isObject()) {
                            howManyReviews++;
                            nestedDocument.append("reviewId", idFromBignum(reviewIdCounter));
                            reviewIdCounter = reviewIdCounter.add(new BigInteger("1"));                            nestedDocument.append("score", arrayNode.get("score").asDouble());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date datt = dateFormat.parse(arrayNode.get("date").asText());
                            nestedDocument.append("date", outputFormat.format(datt));
                            nestedDocument.append("author", arrayNode.get("author").asText());
                        }
                        arrayValues.add(nestedDocument);
                    }
                    document.append(fieldName,arrayValues);
                }
                else if (fieldName.equals("Released")){

                    Vector<SimpleDateFormat> dateFormats = new Vector<>();
                    dateFormats.add(new SimpleDateFormat("MMMM d, yyyy", Locale.US));
                    dateFormats.add(new SimpleDateFormat("MMMM yyyy", Locale.US));
                    dateFormats.add(new SimpleDateFormat("yyyy", Locale.US));

                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String inputString = fieldValue.asText();
                    int indexOfOn = inputString.indexOf("on");
                    String dat = inputString.substring(0,indexOfOn);
                    dat = dat.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
                    String plat = inputString.substring(indexOfOn+3,inputString.length());
                    Document d = new Document();
                    Date date = null;
                    for (SimpleDateFormat format : dateFormats) {
                        try {
                            date = format.parse(dat);
                            break;
                        } catch (ParseException e) {
                            // Do nothing
                        }
                    }
                    if (date == null)
                    {
                        if (!dat.contains("Undated"))
                        {
                            logger.error("Unable to parse date: \"" + dat + "\"");
                        }
                    }
                    String standardizedDate;
                    if (date != null){
                        standardizedDate = outputFormat.format(date);
                    }
                    else standardizedDate = "Undated";
                    d.append("Release Date", standardizedDate);
                    d.append("Platform", plat);
                    document.append(fieldName,d);

                }
                else if (fieldValue.isObject() || fieldValue.isTextual()) {
                    document.append(fieldName, fieldValue.asText());
                } else if (fieldValue.isNumber()){
                    document.append(fieldName, fieldValue.asDouble());
                }
                else if (fieldValue.isArray()) {
                    List<String> arrayValues = new ArrayList<>();
                    for (JsonNode arrayNode : fieldValue) {
                        arrayValues.add(arrayNode.asText());
                    }
                    document.append(fieldName, arrayValues);
                }
            }
            document.append("reviewCount", howManyReviews);
            document.append("Top3ReviewsByLikes", new ArrayList<>());
            documents.add(document);
        }
        reviewIdCounter = new BigInteger("1");
        collection.insertMany(documents);
    }

    private static void insertReviewsAndComments(
        JsonNode jsonNode, 
        JsonNode reviewsInfoJson,
        MongoCollection<Document> reviewsCollection, 
        MongoCollection<Document> commentsCollection) 
    {
        List<Document> reviews = new ArrayList<>();
        List<Document> comments = new ArrayList<>();
        for (JsonNode gameNode : jsonNode) {
            for (JsonNode reviewNode : gameNode.get("reviews")) {
                Integer score = reviewNode.get("score").asInt();
                if (score == 0)
                {
                    if (reviewNode.get("score").asText().equals("0"))
                    {
                        score = 1;
                    }
                    else
                    {
                        logger.warn("Review with non-int score \""+reviewNode.get("score").asText()+"\" found, setting score to null");
                        score = null;
                    }
                }
                JsonNode like_count_node = reviewsInfoJson.get(idFromBignum(reviewIdCounter).toHexString());
                Integer like_count = 0;
                if (like_count_node != null)
                {
                    like_count = like_count_node.intValue();
                }
                Document document = new Document("_id", idFromBignum(reviewIdCounter))
                        .append("game", gameNode.get("Name").asText())
                        .append("score", score)
                        .append("quote", reviewNode.get("quote").asText())
                        .append("author", reviewNode.get("author").asText())
                        .append("date", reviewNode.get("date").asText())
                        .append("likes", like_count);
                for (JsonNode commentNode : reviewNode.get("comments"))
                {
                    Document commentDocument = new Document("_id", idFromBignum(commentIdCounter))
                        .append("reviewId", idFromBignum(reviewIdCounter))
                        .append("author", commentNode.get("author").asText())
                        .append("quote", commentNode.get("quote").asText())
                        .append("date", commentNode.get("date").asText());
                    commentIdCounter = commentIdCounter.add(new BigInteger("1"));
                    comments.add(commentDocument);
                }
                reviewIdCounter = reviewIdCounter.add(new BigInteger("1"));
                reviews.add(document);
            }
        }
        reviewsCollection.insertMany(reviews);
        commentsCollection.insertMany(comments);
    }

    private static ObjectId idFromBignum(BigInteger id) {
        String hex_string = id.toString(16);
        if(hex_string.length() < 24)
        {
            hex_string = "0".repeat(24 - hex_string.length()) + hex_string;
            return new ObjectId(hex_string);
        }
        else
        {
            throw new IllegalArgumentException("BigInteger is too big to be converted to ObjectId");
        }
    }

    private static void insert_one(MongoCollection<Document> collection, Document document, String key) {
        try
        {
            collection.insertOne(document);
        }
        catch (MongoWriteException e)
        {
            if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY))
            {
                logger.warn("Skipped document with key \""+document.get(key)+"\" because it already exists");
            }
            else
            {
                logger.error("Error inserting document with key \""+document.get(key)+"\": "+e.getMessage());
            }
        }
    }

    private static void insertCompanies(JsonNode jsonNode, MongoCollection<Document> collection) {
        // Iterate over each element in the JSON array and insert it into MongoDB
        Iterator<JsonNode> companiesIterator = jsonNode.iterator();
        while (companiesIterator.hasNext()) {
            JsonNode companyNode = companiesIterator.next();
            Document companyDocument = Document.parse(companyNode.toString());
            companyDocument.append("Top3Games",new ArrayList<>());
            companyDocument.append("_id", idFromBignum(companyIdCounter));
            companyIdCounter = companyIdCounter.add(new BigInteger("1"));
            insert_one(collection, companyDocument, "Name");
        }
    }

    private static void insertUsers(JsonNode jsonNode, MongoCollection<Document> collection) {
        // Iterate over each element in the JSON array and insert it into MongoDB
        Iterator<JsonNode> usersIterator = jsonNode.iterator();
        while (usersIterator.hasNext()) {
            JsonNode userNode = usersIterator.next();
            Document userDocument = new Document("_id", idFromBignum(userIdCounter))
                .append("username", userNode.get("username").asText())
                .append("email", userNode.get("email").asText())
                .append("password_hash", userNode.get("password_hash").asText())
                .append("Top3ReviewsByLikes", new ArrayList<>());
            if (userNode.has("company_name"))
            {
                userDocument.append("company_name", userNode.get("company_name").asText());
            }
            else if(userNode.has("is_admin"))
            {
                userDocument.append("is_admin", userNode.get("is_admin").asBoolean());
            }
            userIdCounter = userIdCounter.add(new BigInteger("1"));
            insert_one(collection, userDocument, "username");
        }
    }

    private static void insertUserImages(JsonNode jsonNode, MongoCollection<Document> collection) {
        // Iterate over each element in the JSON array and insert it into MongoDB
        Iterator<JsonNode> usersIterator = jsonNode.iterator();
        while (usersIterator.hasNext()) {
            JsonNode userNode = usersIterator.next();
            Document userDocument = new Document("_id", idFromBignum(userImageIdCounter))
                    .append("username", userNode.get("username").asText())
                    .append("image", userNode.get("image").asText());
            userImageIdCounter = userImageIdCounter.add(new BigInteger("1"));
            insert_one(collection, userDocument, "username");
        }
    }

    @SuppressWarnings("unused")
    private static Date extractDate(String input, SimpleDateFormat dateFormat) throws ParseException {
        // Define a regular expression pattern for date
        Pattern datePattern = Pattern.compile("(\\w+\\s+\\d{1,2}(st|nd|rd|th)?,\\s*\\d{4})");
        Matcher matcher = datePattern.matcher(input);

        if (matcher.find()) {
            String dateString = matcher.group(1).replaceAll("(st|nd|rd|th)", "");
            return dateFormat.parse(dateString);
        } else {
            throw new ParseException("Date not found in the input string", 0);
        }
    }

    @SuppressWarnings("unused")
    private static String extractPlatform(String input) {
        // Define a regular expression pattern for platform
        Pattern platformPattern = Pattern.compile("on\\s+(.*)$");
        Matcher matcher = platformPattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "Platform information not found";
        }
    }
}
