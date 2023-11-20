package org.example;

import com.datastax.oss.driver.api.core.CqlSession;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        List<InetSocketAddress> contactPoints = Arrays.asList(
                new InetSocketAddress("127.0.0.1", 7001),
                new InetSocketAddress("127.0.0.1", 9042)
               // new InetSocketAddress("172.17.0.3", 9042)
                // Add more Cassandra node IPs and ports as needed
        );

        // Connect to the Cassandra cluster
        try (CqlSession session = CqlSession.builder()
                .addContactPoints(contactPoints)
                .withLocalDatacenter("datacenter1") // Replace with your datacenter name if applicable
                .withKeyspace("test_logs") // Replace with your keyspace
                .build()) {
            // Perform operations with Cassandra here
            System.out.println("Connected to Cassandra cluster");
        } catch (Exception e) {
            System.out.println("Failed to connect to Cassandra: " + e.getMessage());
            e.printStackTrace();
        }
    }
}