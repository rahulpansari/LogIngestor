package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import netscape.javascript.JSObject;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.request.IRequest;
import org.example.request.SearchRequest;
import org.example.request.TestRequest;
import org.example.service.DataService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Gson gson=new Gson();
        System.out.println("Hello world!");
        String bootstrapServers = "127.0.0.1:39092"; // Replace with your Kafka broker addresses
        String groupId = "test-group"; // Change to your consumer group ID
        String topicName = "testing"; // Change to your Kafka topic

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singleton(topicName));
        while (true) {
        try {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received message: key = %s, value = %s",
                            record.key(), record.value());
                    TestRequest request=gson.fromJson(record.value(),TestRequest.class);
                    switch (request.getRequestType())
                    {
                        case "INSERT":
                           // TestRequest insertRequest=gson.fromJson(gson.toJson(record.value()),TestRequest.class);
                            DataService.insertData(request);
                            break;
                        case "SEARCH":
                            SearchRequest searchRequest=gson.fromJson(record.value(),SearchRequest.class);
                            DataService.searchData(searchRequest);
                            break;
                        default:
                            System.out.println("No Request Found");
                    }
                }
            }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            // consumer.close();
        }
        }

    }
    }
