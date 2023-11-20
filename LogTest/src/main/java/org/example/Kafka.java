package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class Kafka {
    public static void main(String args[])
    {
        String bootstrapServers = "127.0.0.1:29092"; // Replace with your Kafka broker addresses
        String topicName = "test"; // Change to your Kafka topic

        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
                String message = "{\n" +
                        "\t\"level\": \"error\",\n" +
                        "\t\"requestType\":\"SEARCH\",\n" +
                     //   "\t\"message\": \"Failed to connect to DB\",\n" +
                        "    \"resourceId\": \"server-1234\",\n" +
                        "\t\"traceId\": \"abc-xyz-123\",\n" +
                        "    \"spanId\": \"span-456\",\n" +
                        "    \"commit\": \"5e5342f\"\n" +
                       // "    \"metadata\": {\n" +
                       // "        \"parentResourceId\": \"server-0987\"\n" +
                       // "    }\n" +
                        "}";
                ProducerRecord<String, String> record = new ProducerRecord<>("testing", "message-key", message);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                        System.err.println("Error sending message: " + exception.getMessage());
                    } else {
                        System.out.println("Message sent successfully! Topic: " +
                                metadata.topic() + ", Partition: " + metadata.partition() +
                                ", Offset: " + metadata.offset());
                    }
                });

        } finally {
            producer.flush();
            producer.close();
        }
    }

}
