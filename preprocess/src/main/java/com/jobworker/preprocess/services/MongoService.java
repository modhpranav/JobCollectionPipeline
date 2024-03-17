package com.jobworker.preprocess.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.jobworker.preprocess.models.JobDescriptionModel;
import com.jobworker.preprocess.repositories.JobDescriptionRepository;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoService {

    private final JobDescriptionRepository jobDescriptionRepo;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoService(JobDescriptionRepository jobDescriptionRepo, MongoTemplate mongoTemplate) {
        this.jobDescriptionRepo = jobDescriptionRepo;
        this.mongoTemplate = new MongoTemplate(MongoClients.create(), "jobDescriptionData");
    }

    public void insertJob(JsonNode data, List jobSkills) {
//        String jobDescrition, String jobTitle, String company, String postingUrl, List jobSkills
        JobDescriptionModel job = new JobDescriptionModel(data.get("jobDescription").asText(), data.get("jobTitle").asText(), data.get("company").asText(), data.get("postingURL").asText(), jobSkills);
        this.jobDescriptionRepo.save(job);
    }

    public List<JobDescriptionModel> findAll(){
        return mongoTemplate.findAll(JobDescriptionModel.class, "job");
    }
}


