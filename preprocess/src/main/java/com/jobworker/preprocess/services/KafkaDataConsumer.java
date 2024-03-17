package com.jobworker.preprocess.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobworker.preprocess.models.LlamaResponse;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class KafkaDataConsumer {

    private final LlamaServiceInterface ollamainference;

    private final TextPreprocessor textprocessor;

    private final MongoService mongoService;

    @Autowired
    public KafkaDataConsumer(LlamaServiceInterface ollamainference, TextPreprocessor textprocessor, MongoService mongoService) {
        this.mongoService = mongoService;
        this.ollamainference = ollamainference;
        this.textprocessor = textprocessor;

    }

    @PostConstruct
    public void initialize() throws Exception {
//        ollamainference.initiate("job description");
        List<String> filepaths = Arrays.asList("suggestedSkills.csv", "Skills.csv");
        new ExtractSkills(filepaths);
        System.out.println("Initiated. . .");
    }

    @KafkaListener(topics = "job-details-topic", groupId = "JobCollection-group")
    public void listen(String message) throws IOException {
        System.out.println("Message Recieved. . .");
        JsonNode data = convertToJson(message);
        System.out.println("Processing Job Description. . .");
        String jobDes = textprocessor.process(data.get("jobDescription").asText());
        System.out.println("Fetching Skills in Job Description. . .");
        Set<String> jobSkills =  new FuzzyMatcher().matcher(jobDes);
        System.out.println("Inserting All Data into MongoDB. . .");
        mongoService.insertJob(data, Arrays.asList(jobSkills));
        System.out.println("Process complete for: "+ data.get("postingURL"));

//        String insertStatus = this.insertData(data); Uncomment this line to insert data using alternate method.

        // You can use application.properties and Ollama Service to access LLM model for collecting skills and many more things.
        // Note: You need to set up ollama server in your local and provide its url and application.properties. If you are using Kafka-compose.yml then no need to do anything.
//        LlamaResponse result = ollamainference.getSkills(jobDes); if you have set up lama model then uncomment this line and use it to extract skills.
//        System.out.println(result.getMessage());// Process the message here
    }

    public String insertData(JsonNode data) {
        // This is the alternate method to access mongodb.
        // Connect to MongoDB
        try{
            // Create a connection string
            String connectionString = "mongodb://root:secret@localhost:27017/admin";

            // Create a MongoClientSettings
            ConnectionString connString = new ConnectionString(connectionString);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .build();

            // Create a MongoClient
            MongoClient mongoClient = MongoClients.create(settings);

            // Do something with the MongoClient, e.g., access a database or collection
            // Example:
            mongoClient.listDatabaseNames().forEach(System.out::println);

            // Don't forget to close the MongoClient when done
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }return "NONE";
    }

    private JsonNode convertToJson(String data){

        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to JsonNode
            JsonNode jsonNode = objectMapper.readTree(data);
            return jsonNode;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}