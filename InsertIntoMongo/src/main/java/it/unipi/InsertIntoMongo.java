package it.unipi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class InsertIntoMongo {

    private static int gameIdCounter = 1;
    private static int reviewIdCounter = 1;
    private static int commentIdCounter = 1;

    private static String gamesPath = "./games/commented_games.json";
    private static String companiesPath = "./companies/combined_companies.json";
    private static String configPath = "./InsertIntoMongo/config.json";
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
            logger.info("Loaded config from file");
        }
        catch (IOException e)
        {
            logger.warn("Could not load config from file, using default values");
        }
        try {
            // Connect to MongoDB (assuming it's running locally on the default port)
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");

            MongoClient mongoClient = new MongoClient(uri);
            logger.info("Connected to MongoDB");
            MongoDatabase database = mongoClient.getDatabase("GameCritic");
            database.drop();

            // Load JSON data from a file
            JsonNode jsonNode = objectMapper.readTree(new File(gamesPath));

            // Insert data into the "videogames" collection
            MongoCollection<Document> videoGamesCollection = database.getCollection("videogames");
            insertDynamicData(jsonNode, videoGamesCollection);

            // Insert data into the "reviews" collection
            MongoCollection<Document> reviewsCollection = database.getCollection("reviews");
            insertReviews(jsonNode, reviewsCollection, videoGamesCollection);

            // Insert data into the "comments" collection
            MongoCollection<Document> commentsCollection = database.getCollection("comments");
            insertComments(jsonNode, commentsCollection);
            MongoCollection<Document> collection = database.getCollection("companies");

            // Read JSON file and insert documents into MongoDB
            JsonNode companiesJson = objectMapper.readTree(new File(companiesPath));

            // Iterate over each element in the JSON array and insert it into MongoDB
            Iterator<JsonNode> companiesIterator = companiesJson.iterator();
            while (companiesIterator.hasNext()) {
                JsonNode companyNode = companiesIterator.next();
                Document companyDocument = Document.parse(companyNode.toString());
                companyDocument.append("Top3Games",new ArrayList<>());
                collection.insertOne(companyDocument);
            }

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
            Document document = new Document("id", gameIdCounter++);

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
                            nestedDocument.append("id",reviewIdCounter++);
                            nestedDocument.append("score", arrayNode.get("score").asDouble());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date datt = dateFormat.parse(arrayNode.get("date").asText());
                            nestedDocument.append("date", outputFormat.format(datt));
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

        collection.insertMany(documents);
    }

    private static void insertReviews(JsonNode jsonNode, MongoCollection<Document> reviewsCollection, MongoCollection<Document> videoGamesCollection) {
        List<Document> documents = new ArrayList<>();
        gameIdCounter = 1;
        reviewIdCounter = 1;
        for (JsonNode gameNode : jsonNode) {
            int gameId = gameIdCounter++; // Get the id of the game being reviewed
            for (JsonNode reviewNode : gameNode.get("reviews")) {
                Document document = new Document("id", reviewIdCounter++)
                        .append("gameId", gameId)
                        .append("score", reviewNode.get("score").asText())
                        .append("quote", reviewNode.get("quote").asText())
                        .append("author", reviewNode.get("author").asText())
                        .append("date", reviewNode.get("date").asText())
                        .append("source", reviewNode.get("source").asText());

                documents.add(document);
            }
        }

        reviewsCollection.insertMany(documents);
    }

    private static void insertComments(JsonNode jsonNode, MongoCollection<Document> collection) {
        List<Document> documents = new ArrayList<>();
        reviewIdCounter = 1;
        for (JsonNode gameNode : jsonNode) {
            for (JsonNode reviewNode : gameNode.get("reviews")) {
                int reviewId = reviewIdCounter++; // Get the id of the review being commented
                for (JsonNode commentNode : reviewNode.get("comments")) {
                    Document document = new Document("id", commentIdCounter++)
                            .append("reviewId", reviewId)
                            .append("author", commentNode.get("author").asText())
                            .append("quote", commentNode.get("quote").asText())
                            .append("date", commentNode.get("date").asText())
                            .append("responses", new ArrayList<>());

                    documents.add(document);
                }
            }
        }

        collection.insertMany(documents);
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
