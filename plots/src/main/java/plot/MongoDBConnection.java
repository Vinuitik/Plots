package plot;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDBConnection {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBConnection() {
        mongoClient = MongoClients.create("mongodb://root:example@localhost:27017");  // /admin Connect to MongoDB
        database = mongoClient.getDatabase("self_development");// creates by default
    }

    public void insertData(String collectionName, Date date, int value) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("date", date).append("value", value);
        collection.insertOne(document);
    }
    public List<Document> getData(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> results = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                results.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return results;
    }
}
