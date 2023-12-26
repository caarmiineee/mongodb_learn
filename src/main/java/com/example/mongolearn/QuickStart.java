package com.example.mongolearn;


import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.mongodb.client.model.Filters.*;

public class QuickStart {

    private static String uri = "";

    public static void main(String[] args) throws IOException {
        /*
        findManyMovies();
        insertOneMovies();
        insertManyMovies();
        updateOneMovies();
        updateManyMovies();
        replaceOneMovies();
        deleteOneMovies();
        deleteManyMovies();
        */
        FileReader fileReader = new FileReader("C:\\Users\\Nanosoft\\Desktop\\mongolearn\\src\\main\\resources\\application.properties");
        Properties properties = new Properties();
        properties.load(fileReader);

        uri = properties.getProperty("database.url");

        System.out.println("URL del database: " + uri);
        findOneMovies();
    }

    public static void findOneMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            // Creates instructions to project two document fields
            Bson projectionFields = Projections.fields(
                    Projections.include("title", "genres"),
                    Projections.excludeId());
            // Retrieves the first matching document, applying a projection and a descending sort to the results
            Document doc = collection.find(eq("title", "The Matrix"))
                    .projection(projectionFields)
                    .first();
            // Prints a message if there are no result documents, or prints the result document as JSON
            if (doc == null) {
                System.out.println("No results found.");
            } else {
                System.out.println(doc.toJson());
            }
        }
    }
    public static void findManyMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Bson projectionFields = Projections.fields(
                    Projections.include("title", "releaseYear"),
                    Projections.excludeId());
            // Retrieves documents that match the filter, applying a projection and a descending sort to the results
            MongoCursor<Document> cursor = collection.find(lt("releaseYear", 1970))
                    .projection(projectionFields)
                    .sort(Sorts.descending("releaseYear")).iterator();
            // Prints the results of the find operation as JSON
            try {
                while(cursor.hasNext()) {
                    System.out.println(cursor.next().toJson());
                }
            } finally {
                cursor.close();
            }
        }
    }
    public static void insertOneMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            try {
                // Inserts a sample document describing a movie into the collection
                InsertOneResult result = collection.insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("title", "Ski Bloopers")
                        .append("genres", Arrays.asList("Documentary", "Comedy")));
                // Prints the ID of the inserted document
                System.out.println("Success! Inserted document id: " + result.getInsertedId());

                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }
    }
    public static void insertManyMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            // Creates two sample documents containing a "title" field
            List<Document> movieList = Arrays.asList(
                    new Document().append("title", "Short Circuit 3"),
                    new Document().append("title", "The Lego Frozen Movie"));
            try {
                // Inserts sample documents describing movies into the collection
                InsertManyResult result = collection.insertMany(movieList);
                // Prints the IDs of the inserted documents
                System.out.println("Inserted document ids: " + result.getInsertedIds());

                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }
    }
    public static void updateOneMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Document query = new Document().append("title", "Cool Runnings 2");
            // Creates instructions to update the values of three document fields
            Bson updates = Updates.combine(
                    Updates.set("runtime", 101),
                    Updates.addToSet("genres", "Sports"),
                    Updates.currentTimestamp("lastUpdated"));
            // Instructs the driver to insert a new document if none match the query
            UpdateOptions options = new UpdateOptions().upsert(true);
            try {
                // Updates the first document that has a "title" value of "Cool Runnings 2"
                UpdateResult result = collection.updateOne(query, updates, options);
                // Prints the number of updated documents and the upserted document ID, if an upsert was performed
                System.out.println("Modified document count: " + result.getModifiedCount());
                System.out.println("Upserted id: " + result.getUpsertedId());

                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to update due to an error: " + me);
            }
        }
    }
    public static void updateManyMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Bson query = gt("releaseYear", 2010);
            // Creates instructions to update the values of two document fields
            Bson updates = Updates.combine(
                    Updates.addToSet("genres", "Frequently Discussed"),
                    Updates.currentTimestamp("lastUpdated"));
            try {
                // Updates documents that have a "num_mflix_comments" value over 50
                UpdateResult result = collection.updateMany(query, updates);
                // Prints the number of updated documents
                System.out.println("Modified document count: " + result.getModifiedCount());
                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to update due to an error: " + me);
            }
        }
    }
    public static void replaceOneMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Bson query = eq("title", "50 Violins");
            // Creates a new document containing "title" and "fullplot" fields
            Document replaceDocument = new Document().
                    append("title", "Music of the Heart").
                    append("fullplot", " A dramatization of the true story of Roberta Guaspari who co-founded the Opus 118 Harlem School of Music");
            // Instructs the driver to insert a new document if none match the query
            ReplaceOptions opts = new ReplaceOptions().upsert(true);
            // Replaces the first document that matches the filter with a new document
            UpdateResult result = collection.replaceOne(query, replaceDocument, opts);
            // Prints the number of modified documents and the upserted document ID, if an upsert was performed
            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId());
            // Prints a message if any exceptions occur during the operation
        } catch (MongoException me) {
            System.err.println("Unable to replace due to an error: " + me);
        }
    }
    public static void deleteOneMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Bson query = eq("title", "Music of the Heart");
            try {
                // Deletes the first document that has a "title" value of "Music of the Heart"
                DeleteResult result = collection.deleteOne(query);
                System.out.println("Deleted document count: " + result.getDeletedCount());
                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to delete due to an error: " + me);
            }
        }
    }
    public static void deleteManyMovies() {
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_cinema");
            MongoCollection<Document> collection = database.getCollection("movies");
            Bson query = lt("releaseYear", 1970);
            try {
                // Deletes all documents that have an "imdb.rating" value less than 1.9
                DeleteResult result = collection.deleteMany(query);

                // Prints the number of deleted documents
                System.out.println("Deleted document count: " + result.getDeletedCount());

                // Prints a message if any exceptions occur during the operation
            } catch (MongoException me) {
                System.err.println("Unable to delete due to an error: " + me);
            }
        }
    }
}