package Connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Connections {
    public static MongoClient mongoClient = MongoClients.create("mongodb://ec2-54-68-149-246.us-west-2.compute.amazonaws.com:27017");
    public static MongoDatabase database = mongoClient.getDatabase("assessment");
    public static MongoCollection collection = database.getCollection("paths");
    public static MongoCollection usersCollection = database.getCollection("users");
}
