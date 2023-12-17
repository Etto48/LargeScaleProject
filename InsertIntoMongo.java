import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InsertIntoMongo {

    private static int gameIdCounter = 1;
    private static int reviewIdCounter = 1;
    private static int commentIdCounter = 1;

    private static String gamesPath ="C:/Users/filip/Downloads/commented_games.json";
    private static String companiesPath = "C:\\Users\\filip\\Documents\\Large scale databases\\mongodb_exercise\\htmlRetrieve\\venv\\Scripts\\combined\\combined_companies.json";


    public static void main(String[] args) {
        try {
            // Connect to MongoDB (assuming it's running locally on the default port)
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            MongoDatabase database = mongoClient.getDatabase("GameCritic");

            // Load JSON data from a file
            ObjectMapper objectMapper = new ObjectMapper();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertDynamicData(JsonNode jsonNode, MongoCollection<Document> collection) {
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
                if (fieldName.equals("reviews")) {
                    List<Object> arrayValues = new ArrayList<>();

                    for (JsonNode arrayNode : fieldValue) {
                        Document nestedDocument = new Document();
                        if (arrayNode.isObject()) {
                            howManyReviews++;
                            nestedDocument.append("id",reviewIdCounter++);
                            nestedDocument.append("score", arrayNode.get("score").asDouble());
                        }
                        arrayValues.add(nestedDocument);
                    }
                    document.append(fieldName,arrayValues);
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
}
