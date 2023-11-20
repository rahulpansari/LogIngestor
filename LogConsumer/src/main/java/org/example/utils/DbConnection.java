package org.example.utils;

import com.datastax.oss.driver.api.core.CqlSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class DbConnection {
    private static    CqlSession session =null;
    private static MongoClient mongoClient=null;
    private  static MongoDatabase database=null;
    public static CqlSession getConnection()
    {
        if(session!=null)
            return session;
        List<InetSocketAddress> contactPoints = Arrays.asList(
                new InetSocketAddress("127.0.0.1", 7001),
                new InetSocketAddress("127.0.0.1", 9042)
                // new InetSocketAddress("172.17.0.3", 9042)
                // Add more Cassandra node IPs and ports as needed
        );

        // Connect to the Cassandra cluster
        try {
            // Perform operations with Cassandra here
            CqlSession sessions=CqlSession.builder()
                    .addContactPoints(contactPoints)
                    .withLocalDatacenter("datacenter1") // Replace with your datacenter name if applicable
                    .withKeyspace("test_logs") // Replace with your keyspace
                    .build();
            session=sessions;
            System.out.println("Connected to Cassandra cluster");
            return session;
        } catch (Exception e) {
            System.out.println("Failed to connect to Cassandra: " + e.getMessage());
            e.printStackTrace();
            session.close();
            return null;
        }
    }

    public static MongoClient getMongoConnection()
    {
        if(mongoClient!=null)
            return mongoClient;
        String connectionString = "mongodb://127.0.0.1:9003";

        // Create a connection to the MongoDB cluster
        try{
             mongoClient = MongoClients.create(connectionString) ;


            System.out.println("Connected to MongoDB database");
            return mongoClient;
            // Perform operations on the database
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
        finally {

        }
    }
}
