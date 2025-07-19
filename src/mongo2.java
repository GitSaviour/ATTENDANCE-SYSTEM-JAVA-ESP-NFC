import com.mongodb.client.*;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class mongo2 {
    public static void main(String[] args) {
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("testdb");
        MongoCollection<Document> collection = database.getCollection("students");

        // CREATE
        Document student = new Document("name", "Alice")
                                .append("age", 22)
                                .append("course", "Computer Science");
        collection.insertOne(student);
        System.out.println("Inserted: " + student.toJson());

        // READ
        Document found = collection.find(Filters.eq("name", "Alice")).first();
        System.out.println("Found: " + (found != null ? found.toJson() : "No document found"));

        // UPDATE
        collection.updateOne(Filters.eq("name", "Alice"), Updates.set("age", 23));
        System.out.println("Updated Alice's age to 23");

        // DELETE
        collection.deleteOne(Filters.eq("name", "Alice"));
        System.out.println("Deleted Alice");

        mongoClient.close();
    }
}
